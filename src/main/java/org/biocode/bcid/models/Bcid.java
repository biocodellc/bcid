package org.biocode.bcid.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.apache.commons.lang.StringUtils;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = Bcid.BcidBuilder.class)
@Entity
@Table(name = "bcids")
public class Bcid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private int id;

    @Column(columnDefinition = "bit", name = "ezid_made")
    private boolean ezidMade;

    @Column(columnDefinition = "bit not null", name = "ezid_request")
    private boolean ezidRequest;

    @Convert(converter = UriPersistenceConverter.class)
    private URI identifier;

    private String doi;

    @Column(columnDefinition = "text")
    private String title;

    @Convert(converter = UriPersistenceConverter.class)
    @Column(name = "web_address")
    private URI webAddress;

    @Column(nullable = false, name = "resource_type")
    private String resourceType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Column(updatable = false, insertable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Column(updatable = false, insertable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Type(type = "pg-uuid")
    @Column(name = "user_id")
    private UUID userId;

    private String creator;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "client_id",
            referencedColumnName = "id",
            nullable = false
    )
    private Client client;

    @JsonPOJOBuilder(withPrefix = "")
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

        @JsonCreator
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
            if (StringUtils.isEmpty(title)) {
                title = resourceType;
            }
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

    public void update(Bcid bcid) {
        if (!this.ezidMade()) {
            ezidRequest = bcid.ezidRequest();
        }

        doi = bcid.doi();
        title = bcid.title();
        webAddress = bcid.webAddress();
        userId = bcid.userId();
        creator = bcid.creator();
    }

    public int id() {
        return id;
    }

    public boolean ezidMade() {
        return ezidMade;
    }

    public void setEzidMade(boolean ezidMade) {
        this.ezidMade = ezidMade;
    }

    public boolean ezidRequest() {
        return ezidRequest;
    }

    public void setEzidRequest(boolean ezidRequest) {
        this.ezidRequest = ezidRequest;
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

    public UUID userId() {
        return userId;
    }

    public String creator() {
        return creator;
    }

    public Date modified() {
        return modified;
    }

    public Date created() {
        return created;
    }

    public void setClient(Client client) {
        this.client = client;
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
