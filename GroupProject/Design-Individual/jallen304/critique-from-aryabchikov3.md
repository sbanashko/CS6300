### General Thoughts
#### Pros

The design is concise, it correctly captures most of the relevant domain classes; the textual description provided is in line with the requirements and is complete though necessary to fully comprehend the design decisions
 
#### Cons

The design is "database-centric" overall with much of the functionality implemented or delegated directly to the database proxy class (LocalDataService); an important entity that is not directly represented in the design is player's attempt on a particular cryptogram

### Specific Issues
1.  I like LocalDataService instance in your design (it plays similar role to the xxxRepository classes from my design). However, it is still debatable whether such classes should be shown in the design
1.  I agree with your understanding of ExternalWebService responsibilities
1.  In my opinion, the hierarchy of users has prematurely been collapsed into a single User class (perhaps, an implementation-level decision)
1.  W.r.t. Reqs.#6,9 it is not quite clear which class is responsible for performing solution phrase encryption (the EncodedPhrase attribute is filled with). Probably there is a missing notion of cryptographic algorithm providing its services to either Cryptogram or User. I made a similar mistake in my design by mixing up responsibilities of Cryptogram and SimpleSubstitutionCipher.
1.  Cryptogram class. I disagree that Solution, Solved, and CurrentProgress are the properties of Cryptogram. These are likely the properties of an association between Cryptogram and User/Player or some standalone class (e.g. Attempt/PotentialSolution).
1.  Req.#10 (_"At any point, the player may return to the list of cryptograms to try another one."_). It is not quite clear from the design how the partial (not yet submitted)  solution is saved if the user decided to abandon his current attempt on a cryptogram without submitting his solution (Solve() has not been called). There is probably an unstated assumption on this.
1.  Likewise, I disagree that Solved and WrongAttempts are the properties of the Cryptogram class.
1.  I concur with the presence of a Rating class as a specification of a contract (between e.g. a User and an ExternalWebService). However, I am not sure if it is properly modeled as an association class. From my point of view it is basically a convenience structure since all its fields can be either queried from the ExternalWebService or filled in via appropriate methods of a User based on a associated collection of his attempts (Attempt/PotentialSolution as mentioned above) over cryptograms. There was also a discussion about Ratings in Slack.
