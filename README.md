# Biocode Commons Identifiers

Biocode Commons Identifiers (BCIDs) are a special kind of identifier that enables tracking and 
managing physical objects or processes. 
Historically, tracking and managing samples or processes and their derivatives across institutions has proved 
to be a challenge and typically suffer from one or more drawbacks that undermine their utility, 
including lack of a long term persistence strategy, and resolvability.

## Principles

  * Global Uniqueness.  All BCIDs are globally unique.
  * Opacity. URLs, The “Darwin Core Triplet”, and LSIDs implies a level of ownership on the sample itself that 
  does not relate directly to an institution or collection.  That is, the object did not arise 
  due to a collecting event or other process, the collecting event or process merely isolates, 
  transforms, or acknowledges an entity that already existed.  Hence, any identification scheme 
  that includes words or acronyms that could be seen to suggest ownership violates the principle 
  of samples as being part of the commons.
  * Persistence.  Most projects generating identifiers have < 10 year life-spans.  Having persistent 
  identifiers means we need to think about strategies for resolution services (if required) that have a > 10 year 
  lifespan and in the context of an institution that is designed to be persistent. 
  * Source Generation. The BCID should be assigned as close as possible to the sampling event as possible.
  * Propagation.  Aggregators, borrowers, and any downstream users must honor the original BCID assignment 
  by representing the BCID in its explicit form in any aggregator, disseminator, or publisher of data.

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

```https://n2t.net/ark:/21547/R2MBIO0056```
 
  * *https://n2t.net/*: resolution target (can substitute different targets here)
  * *ark*: Scheme
  * *21547*: Name Assigning Authority
  * *R2*: Group identifier includes all characters including up to and including the number 2
  * *MBIO0056*: Locally unique identifier (optional)

Licensing Terms

A BCID must always propagate from source to source and must always be displayed visually and/or embedded in content depending on medium:

HTML pages -- icon w/ link to identifier and RDFa tag
Print Text -- standard text with written link
Images, PDF, TIFF -- develop strategy for this

The BCID system will redirect to the _target (webAddress) where a human or other agent can discover further information about the resource, with the addition of a banner, frame, or document element that discloses the license agreement terms for BCID use.  The mechanism of this is to encode the _target with the suggested URL: http://license/?url=redirectionurl

## Value Added Services

BCIDs will be free for users to implement and offer the ability to use them as outlined in this document.   Value added services are being explored which would provide one or more of the following services:
Tracking BCID usage across collections -- this includes performing policing of identifiers to ensure that duplicate identifiers are not minted down workflow chains and that BCIDs maintain their viral nature and are used properly.  Methods of policing:
heuristic (e.g. VertNet approach of matching clumping metadata)
user surveys, research on identifier implementation
Change/update notification on metadata elements associated with identifiers.  This is an implementation of the BiSciCol tracker, and run for the benefit of those with BCIDs.


API
1. Pre-Mint blocks of identifiers.  
This essentially reserves a block of identifiers for users with no attached metadata.

2. Mint BCID/LocalID identifiers
Allow the user to keep their local identifier as an attachment to a BCID with attached metadata

3. Mint BCID
Mint a BCID with attached metadata
