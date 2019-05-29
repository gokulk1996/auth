package com.cme.budderfly.authenticate.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Lob;
import java.io.Serializable;
import java.util.Objects;

public class ContactDTO implements Serializable {

    private Long id;

    @JsonProperty("ID")
    private String zohoId;

    @JsonProperty("Contact_Type")
    private String contactType;

    @JsonProperty("Approved")
    private Boolean approved;

    @JsonProperty("Agreement")
    private Boolean agreement;

    @JsonProperty("Company_Name")
    private String companyName;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Contact_Email")
    private String contactEmail;

    @JsonProperty("CC_Emails.Email")
    private String ccEmails;

    @JsonProperty("Phone_Number")
    private String phoneNumber;

    @JsonProperty("Contact_Address")
    private String contactAddress;

    @JsonProperty("State")
    private String state;

    @JsonProperty("Location_Valid")
    private Boolean locationValid;

    @JsonProperty("Latitude")
    private String latitude;

    @JsonProperty("Longitude")
    private String longitude;

    @JsonProperty("Webpage")
    private String webpage;

    @Lob
    @JsonProperty("About_me")
    private String notes;

    @JsonProperty("User")
    private String user;

    @JsonProperty("Service_Range")
    private Integer serviceRange;

    @JsonProperty("Day_Time_Hourly")
    private Float dayTimeHourly;

    @JsonProperty("Day_Time_Blended")
    private Float dayTimeBlended;

    @JsonProperty("Night_Time_Hourly")
    private Float nightTimeHourly;

    @JsonProperty("Night_Time_Blended")
    private Float nightTimeBlended;

    @JsonProperty("Other_Fees")
    private String otherFees;

    @JsonProperty("Travel_Fees")
    private String travelFees;

    @JsonProperty("Contractor_Specialty")
    private String contractorSpecialty;

    @JsonProperty("Rating")
    private String rating;

    private Boolean removedInZoho = false;

    @JsonProperty("City")
    private String city;

    @JsonProperty("Street")
    private String street;

    @JsonProperty("Zip_Code")
    private String zipCode;

    @JsonProperty("Country")
    private String country;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getZohoId() {
        return zohoId;
    }

    public void setZohoId(String zohoId) {
        this.zohoId = zohoId;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public Boolean isApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Boolean isAgreement() {
        return agreement;
    }

    public void setAgreement(Boolean agreement) {
        this.agreement = agreement;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getCcEmails() {
        return ccEmails;
    }

    public void setCcEmails(String ccEmails) {
        this.ccEmails = ccEmails;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean isLocationValid() {
        return locationValid;
    }

    public void setLocationValid(Boolean locationValid) {
        this.locationValid = locationValid;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getWebpage() {
        return webpage;
    }

    public void setWebpage(String webpage) {
        this.webpage = webpage;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getServiceRange() {
        return serviceRange;
    }

    public void setServiceRange(Integer serviceRange) {
        this.serviceRange = serviceRange;
    }

    public Float getDayTimeHourly() {
        return dayTimeHourly;
    }

    public void setDayTimeHourly(Float dayTimeHourly) {
        this.dayTimeHourly = dayTimeHourly;
    }

    public Float getDayTimeBlended() {
        return dayTimeBlended;
    }

    public void setDayTimeBlended(Float dayTimeBlended) {
        this.dayTimeBlended = dayTimeBlended;
    }

    public Float getNightTimeHourly() {
        return nightTimeHourly;
    }

    public void setNightTimeHourly(Float nightTimeHourly) {
        this.nightTimeHourly = nightTimeHourly;
    }

    public Float getNightTimeBlended() {
        return nightTimeBlended;
    }

    public void setNightTimeBlended(Float nightTimeBlended) {
        this.nightTimeBlended = nightTimeBlended;
    }

    public String getOtherFees() {
        return otherFees;
    }

    public void setOtherFees(String otherFees) {
        this.otherFees = otherFees;
    }

    public String getTravelFees() {
        return travelFees;
    }

    public void setTravelFees(String travelFees) {
        this.travelFees = travelFees;
    }

    public String getContractorSpecialty() {
        return contractorSpecialty;
    }

    public void setContractorSpecialty(String contractorSpecialty) {
        this.contractorSpecialty = contractorSpecialty;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Boolean isRemovedInZoho() {
        return removedInZoho;
    }

    public void setRemovedInZoho(Boolean removedInZoho) {
        this.removedInZoho = removedInZoho;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ContactDTO contactDTO = (ContactDTO) o;
        if(contactDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), contactDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ContactDTO{" +
            "id=" + getId() +
            ", zohoId='" + getZohoId() + "'" +
            ", contactType='" + getContactType() + "'" +
            ", approved='" + isApproved() + "'" +
            ", agreement='" + isAgreement() + "'" +
            ", companyName='" + getCompanyName() + "'" +
            ", name='" + getName() + "'" +
            ", contactEmail='" + getContactEmail() + "'" +
            ", ccEmails='" + getCcEmails() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", contactAddress='" + getContactAddress() + "'" +
            ", state='" + getState() + "'" +
            ", locationValid='" + isLocationValid() + "'" +
            ", latitude='" + getLatitude() + "'" +
            ", longitude='" + getLongitude() + "'" +
            ", webpage='" + getWebpage() + "'" +
            ", notes='" + getNotes() + "'" +
            ", user='" + getUser() + "'" +
            ", serviceRange=" + getServiceRange() +
            ", dayTimeHourly=" + getDayTimeHourly() +
            ", dayTimeBlended=" + getDayTimeBlended() +
            ", nightTimeHourly=" + getNightTimeHourly() +
            ", nightTimeBlended=" + getNightTimeBlended() +
            ", otherFees='" + getOtherFees() + "'" +
            ", travelFees='" + getTravelFees() + "'" +
            ", contractorSpecialty='" + getContractorSpecialty() + "'" +
            ", rating='" + getRating() + "'" +
            ", removedInZoho='" + isRemovedInZoho() + "'" +
            ", city='" + getCity() + "'" +
            ", street='" + getStreet() + "'" +
            ", zipCode='" + getZipCode() + "'" +
            ", country='" + getCountry() + "'" +
            "}";
    }
}
