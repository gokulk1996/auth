#!/bin/bash
 
# Some credit to https://github.com/maxtsepkov/bash_colors/blob/master/bash_colors.sh
#
# Constants and functions for terminal colors. Not using tput
# Author: Steve Wyckoff
 
COLOR_SCRIPT=color-logger.bash
COLOR_VERSION=0.9.0
 
COLOR_ESC="\x1B["
 
COLOR_RESET=0             # reset all attributes to their defaults
COLOR_RESET_UNDERLINE=24  # underline off
COLOR_RESET_REVERSE=27    # reverse off
COLOR_DEFAULT=39          # set underscore off, set default foreground color
COLOR_DEFAULTB=49         # set default background color
 
COLOR_BOLD=1              # set bold
COLOR_BRIGHT=2            # set half-bright (simulated with color on a color display)
COLOR_UNDERSCORE=4        # set underscore (simulated with color on a color display)
COLOR_REVERSE=7           # set reverse video
 
COLOR_BLACK=30            # set black foreground
COLOR_RED=31              # set red foreground
COLOR_GREEN=32            # set green foreground
COLOR_BROWN=33            # set brown foreground
COLOR_BLUE=34             # set blue foreground
COLOR_MAGENTA=35          # set magenta foreground
COLOR_CYAN=36             # set cyan foreground
COLOR_WHITE=37            # set white foreground
 
COLOR_BLACK_BG=40           # set black background
COLOR_RED_BG=41             # set red background
COLOR_GREEN_BG=42           # set green background
COLOR_BROWN_BG=43           # set brown background
COLOR_BLUE_BG=44            # set blue background
COLOR_MAGENTA_BG=45         # set magenta background
COLOR_CYAN_BG=46            # set cyan background
COLOR_WHITE_BG=47           # set white background
 
COLOR_DEBUG=$COLOR_BLUE
COLOR_INFO=$COLOR_MAGENTA
COLOR_HIGHLIGHT=$COLOR_CYAN
COLOR_WARN=$COLOR_BROWN
COLOR_ERROR=$COLOR_RED
 
COLOR_SUCCESS=$COLOR_GREEN
 
logger_wrap_escape() {
  local paint="$1"
  local message="$2"
 
  if ! [ $paint -ge 0 -a $paint -le 47 ] 2>/dev/null; then
    echo "escape: argument for \"$paint\" is out of range" >&2 && return 1
  fi
 
  if [ -z "$message" ]; then
    echo "No message passed in"
 
    exit 1
  fi
 
  message="${COLOR_ESC}${paint}m${message}${COLOR_ESC}${COLOR_RESET}m"
 
  echo -ne "\n$message\n"
}
 
logger_color(){
  local paint="$1"
  shift
 
  for message in "$@";
  do
    logger_wrap_escape "$paint" "$message"
  done
 
  echo
}
 
# PUBLIC API
debug(){
  logger_color "$COLOR_DEBUG" "$@"
}
 
info(){
  logger_color "$COLOR_INFO" "$@"
}
 
warn(){
  logger_color "$COLOR_WARN" "$@"
}
 
error(){
  logger_color "$COLOR_ERROR" "$@"
}
 
highlight(){
  logger_wrap_escape $COLOR_HIGHLIGHT "$1"
}
 
success(){
  logger_color "$COLOR_SUCCESS" "$@"
}
 
die () {
    error "$@"
    exit 1
}
 
[ "$#" -eq 2 ] || die "2 arguments are required, $# provided, please provide an env and version. $(highlight 'Usage: ./deploy.sh qa1ms 1.0.0')"
 
ENV_QA="qa1ms"
ENV_PROD="prod1ms"
 
if [[ "$1" != "$ENV_QA" && "$1" != "$ENV_PROD" ]]; then
  die "Expected $(highlight $ENV_QA) or $(highlight $ENV_PROD) as parameter values."
fi
 
if [[ "$1" = "$ENV_QA" ]]; then
  debug "Tag versions for QA AWS ECR"
  docker tag authenticate:v$2 960527451976.dkr.ecr.us-west-2.amazonaws.com/qa/authenticate:latest
  docker tag authenticate:v$2 960527451976.dkr.ecr.us-west-2.amazonaws.com/qa/authenticate:v$2
  info "$(docker images | grep $2 | grep amazonaws | grep authenticate | grep qa)"
  info "$(docker images | grep latest | grep amazonaws | grep authenticate | grep qa)"
fi
 
if [ "$1" = "$ENV_PROD" ]; then
  debug "Tag versions for PROD AWS ECR"
  docker tag authenticate:v$2 181564704724.dkr.ecr.us-west-2.amazonaws.com/prod/authenticate:latest
  docker tag authenticate:v$2 181564704724.dkr.ecr.us-west-2.amazonaws.com/prod/authenticate:v$2
  info "$(docker images | grep $2 | grep amazonaws | grep authenticate | grep prod)"
  info "$(docker images | grep latest | grep amazonaws | grep authenticate | grep prod)"
fi
 
AWS_ECR_DOCKER_IMG_DIGEST=""
debug "Login to AWS"
if [[ "$1" = "$ENV_QA" ]]; then
login=$(aws ecr get-login --no-include-email --region us-west-2 --profile qa)
fi

if [ "$1" = "$ENV_PROD" ]; then
login=$(aws ecr get-login --no-include-email --region us-west-2)
fi

eval $login
 
if [[ "$1" = "$ENV_QA" ]]; then
  debug "Pushing docker images to QA AWS ECR"
  docker push 960527451976.dkr.ecr.us-west-2.amazonaws.com/qa/authenticate:latest
  docker push 960527451976.dkr.ecr.us-west-2.amazonaws.com/qa/authenticate:v$2
  AWS_ECR_DOCKER_IMG_DIGEST=$(aws ecr describe-images --repository-name qa/authenticate --query 'sort_by(imageDetails,& imagePushedAt)[-1].imageDigest' --region us-west-2 --profile qa | tr -d '"')
fi
 
if [ "$1" = "$ENV_PROD" ]; then
  debug "Pushing docker images to PROD AWS ECR"
  docker push 181564704724.dkr.ecr.us-west-2.amazonaws.com/prod/authenticate:latest
  docker push 181564704724.dkr.ecr.us-west-2.amazonaws.com/prod/authenticate:v$2
  AWS_ECR_DOCKER_IMG_DIGEST=$(aws ecr describe-images --repository-name prod/authenticate --query 'sort_by(imageDetails,& imagePushedAt)[-1].imageDigest' | tr -d '"')
fi

DOCKER_IMG_DIGEST=$(docker inspect --format='{{index .RepoDigests 0}}' authenticate:v$2 | cut -d'@' -f2)
debug "Validating docker images in AWS ECR"
if [[ "$DOCKER_IMG_DIGEST" = "$AWS_ECR_DOCKER_IMG_DIGEST" ]]; then
    debug "Docker image has been pushed successfully to AWS ECR."
else
    debug "$DOCKER_IMG_DIGEST == $AWS_ECR_DOCKER_IMG_DIGEST ?"
    die "Docker image mismatch, digests don't match, please check."
fi

debug "Using $1 Kubernetes cluster"
 
kubectl config use-context $1
 
debug "Updating current version of authenticate app"
 
if [[ "$1" = "$ENV_QA" ]]; then
  debug "Starting Authenticate app on QA kubernetes cluster"
  kubectl apply -f ../minikube/k8s-qa/authenticate
fi
 
if [ "$1" = "$ENV_PROD" ]; then
  debug "Starting Authenticate app on PROD kubernetes cluster"
  kubectl apply -f ../minikube/k8s-prod/authenticate
fi

## https://github.com/kubernetes/kubernetes/issues/33664
## there is no other way to force an image repull without having different
## image tags per release, with the following line we force it by adding a dummy
## environment variable
kubectl set env deployment/authenticate DEPLOY_DATE="$(date)"
 
debug "Authenticate pods:"
kubectl get pods | grep authenticate | grep -v web
 
success "Deploy finished, check app logs with $(highlight 'kubectl logs -f <authenticate-hash>')"
