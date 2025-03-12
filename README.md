## README.md

# Documentation

- [Installation Guide](installation.md) - Instructions to set up and configure BCID.
- [API Documentation](api.md) - Details on available API endpoints and usage.



# About Biocode Commons Identifiers

Biocode Commons Identifiers (BCIDs) enable tracking and 
managing physical objects or processes by registering a single parent that provides resolution for
multiple children, provided as suffixes to the registered identifier.  The BCID itself is a reference
to the "parent" object and any "children" are implicitly descended from the parent as a result of 
a copy or division of physical material or a result of a process.
A BCID is a single identifier which can resolve potentially
millions of discrete "child" objects by appending any conforming locally unique identifier to the parent.

## Principles

  * Global Uniqueness.  All BCIDs are globally unique.
  * Opacity. The parent URI does not include any reference to suggest ownership or identity.
  * Persistence.  Our goal is to persist object resolution > 25 years into the future.
  * Source Generation. The BCID should be assigned as close as possible to the sampling event as possible.
  * Propagation.  Aggregators and downstream users agree to represent the BCID in its explicit form.

## Identifier Metadata

Metadata is assigned using an API.  The following metadata elements are encoded with the identifier 
string and can be extracted/queried from the identifier:

  * Who
  * When
  * Where
  * What
  * Abstract


## Identifier Resolution and Construction

EZID offers the n2t.net resolver for ARKs.  BCIDs take the following form:

``` https://n2t.net/ark:/21547/CXs2MBIO1044 ```
 
  * **https://n2t.net/**: resolution target (can substitute different targets here)
  * **ark**: Scheme
  * **21547**: Name Assigning Authority
  * **CXs2**: Group identifier includes all characters including up to and including the number 2. This stands for a set of entities belonging to the same type of entity (typically assigned the same ontology class)
  * **MBIO1044**: Locally unique identifier (optional)
  * `curl https://ezid.cdlib.org/id/ark:/21547/CXs2`: Fetch the Group identifier metadata, all text after the last "/" up until the first integer.
  ```
  success: ark:/21547/CXs2
  dc.date: 2019-02-28
  dc.type: http://rs.tdwg.org/dwc/terms/MaterialSample
  dc.title: Sample
  dc.creator: creator_email@service.com
  dc.publisher: GeOMe-db FIMS
  _owner: bcid
  _ownergroup: ucblibrary
  _created: 1551397078
  _updated: 1560274877
  _profile: dc
  _target: https://geome-db.org/record/ark:/21547/CXs2
  _status: public
  _export: yes
  ```

Licensing Terms

A BCID must always propagate from source to source and must always be displayed visually and/or embedded in content depending on medium:

HTML pages -- icon w/ link to identifier and RDFa tag
Print Text -- standard text with written link
Images, PDF, TIFF -- develop strategy for this

The BCID system will redirect to the _target (webAddress) where a human or other agent can discover further information about the resource, with the addition of a banner, frame, or document element that discloses the license agreement terms for BCID use.  The mechanism of this is to encode the _target with the suggested URL: http://license/?url=redirectionurl

## Value Added Services

BCIDs will be free for users to implement and offer the ability to use them as outlined in this document.   Services not included here are:
 * Tracking BCID usage across collections -- this includes performing policing of identifiers to ensure that duplicate identifiers are not minted down workflow chains and that BCIDs maintain their viral nature and are used properly.  Methods of policing:
heuristic (e.g. VertNet approach of matching clumping metadata)
user surveys, research on identifier implementation
 * Change/update notification on metadata elements associated with identifiers.  This is an implementation of the BiSciCol tracker, and run for the benefit of those with BCIDs.
