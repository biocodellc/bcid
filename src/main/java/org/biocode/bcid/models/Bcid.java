package org.biocode.bcid.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.biocode.bcid.ezid.EzidRequestType;
import org.springframework.util.Assert;

import javax.ws.rs.BadRequestException;
import java.net.URI;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Bcid Entity object
 */
@JsonIgnoreProperties({"created", "requestType"})
@JsonDeserialize(builder = Bcid.BcidBuilder.class)
public class Bcid {
    private URI identifier;
    private String doi;
    private String title;
    private URI webAddress;
    private String resourceType;
    private Date created;
    private String creator;
    private String publisher;
    private EzidRequestType requestType;

    private Bcid(BcidBuilder builder) {
        resourceType = builder.resourceType;
        doi = builder.doi;
        title = builder.title;
        webAddress = builder.webAddress;
        creator = builder.creator;
        publisher = builder.publisher;
    }

    public URI identifier() {
        return identifier;
    }

    public void setIdentifier(URI identifier) {
        if (this.identifier == null)
            this.identifier = identifier;
    }

    public String doi() {
        return doi;
    }

    public String title() {
        return title;
    }

    public URI webAddress() {
        return webAddress;
    }

    public void setWebAddress(URI webAddress) {
        isValidUrl(webAddress);
        this.webAddress = webAddress;
    }

    public String resourceType() {
        return resourceType;
    }

    public Date created() { return created; }

    public void setCreated(Date dateTime) {
        this.created = dateTime;
    }

    public String creator() {
        return creator;
    }

    public String publisher() {
        return publisher;
    }

    public EzidRequestType requestType() { return requestType; }

    public void setRequestType(EzidRequestType requestType) {
        this.requestType = requestType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bcid)) return false;

        Bcid bcid = (Bcid) o;

        return identifier != null ? identifier.equals(bcid.identifier) : bcid.identifier == null;
    }

    @Override
    public int hashCode() {
        return identifier != null ? identifier.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Bcid{" +
                "identifier=" + identifier +
                ", doi='" + doi + '\'' +
                ", title='" + title + '\'' +
                ", webAddress=" + webAddress +
                ", resourceType='" + resourceType + '\'' +
                ", created=" + created +
                ", creator='" + creator + '\'' +
                ", publisher='" + publisher + '\'' +
                ", requestType=" + requestType +
                '}';
    }

    private static void isValidUrl(URI webAddress) {
        if (webAddress != null) {
            String[] schemes = {"http", "https"};
            UrlValidator urlValidator = new UrlValidator(schemes);
            if (!urlValidator.isValid(String.valueOf(webAddress)))
                throw new BadRequestException("Invalid URL for bcid webAddress");
        }
    }


    @JsonPOJOBuilder(withPrefix = "")
    public static class BcidBuilder {

        // Required parameters
        private String resourceType;
        private String creator;
        private String publisher;

        //Optional parameters
        private String doi;
        private String title;
        private URI webAddress;
        private UUID userId;

        @JsonCreator
        public BcidBuilder(String resourceType, String creator, String publisher) {
            Assert.notNull(resourceType, "Bcid resourceType must not be null");
            Assert.notNull(creator, "Bcid creator must not be null");
            Assert.notNull(publisher, "Bcid publisher must not be null");

            this.resourceType = resourceType;
            this.creator = creator;
            this.publisher = publisher;
        }


        public BcidBuilder doi(String val) {
            doi = val;
            return this;
        }

        public BcidBuilder title(String val) {
            title = val;
            return this;
        }

        public BcidBuilder webAddress(URI val) {
            isValidUrl(val);
            webAddress = val;
            return this;
        }

        public BcidBuilder userId(UUID val) {
            userId = val;
            return this;
        }

        public Bcid build() {
            if (StringUtils.isEmpty(title)) {
                title = resourceType;
            }
            return new Bcid(this);
        }

    }
}
