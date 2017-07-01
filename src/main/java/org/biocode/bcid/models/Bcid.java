package org.biocode.bcid.models;

import org.apache.commons.validator.routines.UrlValidator;
import org.hibernate.annotations.Type;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.ws.rs.BadRequestException;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

/**
 * Bcid Entity object
 */
@Entity
@Table(name = "bcids")
public class Bcid {
    private int id;
    private boolean ezidMade;
    private boolean ezidRequest;
    private URI identifier;
    private String doi;
    private String title;
    private URI webAddress;
    private String resourceType;
    private Date created;
    private Date modified;
    private UUID userId;
    private String creator;

    public static class BcidBuilder {

        // Required parameters
        private String resourceType;
        private String creator;

        //Optional parameters
        private boolean ezidRequest = true;
        private String doi;
        private String title;
        private URI webAddress;
        private UUID userId;

        public BcidBuilder(String resourceType, String creator) {
            Assert.notNull(resourceType, "Bcid resourceType must not be null");
            Assert.notNull(creator, "Bcid creator must not be null");

            this.resourceType = resourceType;
            this.creator = creator;
        }

        public BcidBuilder ezidRequest(boolean val) {
            ezidRequest = val;
            return this;
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
            return new Bcid(this);
        }

    }

    private Bcid(BcidBuilder builder) {
        resourceType = builder.resourceType;
        ezidRequest = builder.ezidRequest;
        doi = builder.doi;
        title = builder.title;
        webAddress = builder.webAddress;
        creator = builder.creator;
        userId = builder.userId;
    }

    // needed for hibernate
    Bcid() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int id() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    @Column(columnDefinition = "bit", name = "ezid_made")
    public boolean ezidMade() {
        return ezidMade;
    }

    public void setEzidMade(boolean ezidMade) {
        this.ezidMade = ezidMade;
    }

    @Column(columnDefinition = "bit not null", name = "ezid_request")
    public boolean ezidRequest() {
        return ezidRequest;
    }

    public void setEzidRequest(boolean ezidRequest) {
        this.ezidRequest = ezidRequest;
    }

    @Convert(converter = UriPersistenceConverter.class)
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

    public void setDoi(String doi) {
        this.doi = doi;
    }

    @Column(columnDefinition = "text")
    public String title() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Convert(converter = UriPersistenceConverter.class)
    @Column(name = "web_address")
    public URI webAddress() {
        return webAddress;
    }

    public void setWebAddress(URI webAddress) {
        isValidUrl(webAddress);
        this.webAddress = webAddress;
    }

    @Column(nullable = false, name = "resource_type")
    public String resourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    @Type(type="pg-uuid")
    @Column(name="user_id")
    public UUID userId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String creator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date modified() {
        return modified;
    }

    private void setModified(Date modified) {
        this.modified = modified;
    }

    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date created() {
        return created;
    }

    private void setCreated(Date created) {
        this.created = created;
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
                "id=" + id +
                ", ezidMade=" + ezidMade +
                ", ezidRequest=" + ezidRequest +
                ", identifier=" + identifier +
                ", doi='" + doi + '\'' +
                ", title='" + title + '\'' +
                ", webAddress=" + webAddress +
                ", resourceType='" + resourceType + '\'' +
                ", created=" + created +
                ", modified=" + modified +
                ", userId=" + userId +
                ", creator='" + creator + '\'' +
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
}
