package org.biocode.bcid.models;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ServerErrorException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * Bcid Entity object
 */
@Entity
@Table(name = "bcids")
public class Bcid {
    private int bcidId;
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

    public static class BcidBuilder {

        // Required parameters
        private String resourceType;
        //Optional parameters
        private boolean ezidMade = false;

        private String subResourceType;
        private boolean ezidRequest = true;
        private String doi;
        private String title;
        private URI webAddress;
        private String graph;
        private String sourceFile;

        public BcidBuilder(String resourceType) {
            Assert.notNull(resourceType, "Bcid resourceType must not be null");
            this.resourceType = resourceType;
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

        public Bcid build() {
            return new Bcid(this);
        }

    }

    private Bcid(BcidBuilder builder) {
        resourceType = builder.resourceType;
        ezidMade = builder.ezidMade;
        ezidRequest = builder.ezidRequest;
        doi = builder.doi;
        title = builder.title;
        webAddress = builder.webAddress;
    }

    // needed for hibernate
    Bcid() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int getBcidId() {
        return bcidId;
    }

    private void setBcidId(int id) {
        this.bcidId = id;
    }

    @Column(columnDefinition = "bit", name = "ezid_made")
    public boolean isEzidMade() {
        return ezidMade;
    }

    public void setEzidMade(boolean ezidMade) {
        this.ezidMade = ezidMade;
    }

    @Column(columnDefinition = "bit not null", name = "ezid_request")
    public boolean isEzidRequest() {
        return ezidRequest;
    }

    public void setEzidRequest(boolean ezidRequest) {
        this.ezidRequest = ezidRequest;
    }

    @Convert(converter = UriPersistenceConverter.class)
    public URI getIdentifier() {
        return identifier;
    }

    public void setIdentifier(URI identifier) {
        if (this.identifier == null)
            this.identifier = identifier;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    @Column(columnDefinition = "text")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Convert(converter = UriPersistenceConverter.class)
    @Column(name = "web_address")
    public URI getWebAddress() {
        // TODO move the following to the BcidService.create after all Bcid creation is done via BcidService class
        if (identifier != null && webAddress != null && webAddress.toString().contains("%7Bark%7D")) {
            try {
                webAddress = new URI(StringUtils.replace(
                        webAddress.toString(),
                        "%7Bark%7D",
                        identifier.toString()));
            } catch (URISyntaxException e) {
                throw new ServerErrorException(500, e);
            }
        }
        return webAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bcid)) return false;

        Bcid bcid = (Bcid) o;
        return getIdentifier() != null && Objects.equals(getIdentifier(), bcid.getIdentifier());
    }

    @Override
    public int hashCode() {
        return 31;
    }

    public void setWebAddress(URI webAddress) {
        isValidUrl(webAddress);
        this.webAddress = webAddress;
    }

    @Column(nullable = false, name = "resource_type")
    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreated() {
        return created;
    }

    private void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "Bcid{" +
                "bcidId=" + bcidId +
                ", ezidMade=" + ezidMade +
                ", ezidRequest=" + ezidRequest +
                ", identifier='" + identifier + '\'' +
                ", doi='" + doi + '\'' +
                ", title='" + title + '\'' +
                ", webAddress='" + webAddress + '\'' +
                ", resourceType='" + resourceType + '\'' +
                ", created=" + created +
                ", modified=" + modified +
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
