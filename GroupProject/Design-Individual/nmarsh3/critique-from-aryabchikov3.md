### Overall Thoughts

Overall, the design covers all essential domain entities of the system, including external services modeled as utility classes.
The design, however, does not pay much attention to class responsibilities (methods of the classes) and has minor uml issues.

### Specific Issues 

#### Pros

1.  I generally agree with the nomenclature of entities presented in the design. The design is clean and not overwhelmed by unnecessary low-level details.

1.  The design, in my opinion, covers all major Requirements of Boston Towers Co. (subject to interpretation details)

1.  I like the notion of a CipherService utility used by a Cryptogram to compute the cipher. Correctly, applyCipherToString/encode is a method of CipherService (I was wrong making it Cryptogram's method). I can also think of a Strategy pattern representing a CipherService as one of possible implementations of a generic crypto algorithm (base class or interface), used by Cryptogram class.

#### Cons

1.  Dashed lined with hollow arrows represent interface implementations in UML; these should probably be replaced by "dependency" relations.
>Yep, I think that was supposed to be a dashed line with a solid arrow. 

1.  CryptogramAttempt - Player association multiplicity specification is probably reversed (One Player has * Attempts, whereas an Attempts is made by a single Player). I would model this as n-to-n association between Player and Cryptogram with CryptogramAttempt being an association class.
> CryptogramAttempt being an explicit class was deliberate - Notice the aggregation relationship between Cryptogram and CryptogramAttempt - CryptogramAttempt is actually a concrete instantiation of the Cryptogram class.
> I thought I had the multiplicity correct? A CryptogramAttempt is aware of 0-1 players, and a player is aware of 0..n cryptogramAttempts. Did I get those reversed?

1.  Cryptogram class
	*  it is arguable that cipher should be a property of Cryptogram. Will just a "solution" be sufficient to verify Player's answer once the actual solution gets encrypted?  
> Per discussion in slack, you can definitely just compare the solution to the player's answer. A Cryptogram doesn't really care about the cipher used - it only needs to know 'does the supplied solution = my solution?'
	
1.  ExternalWebService class
	*  is generatePlayerId() actually provided by the service (follow from the Reqs)?
	*  same for getHistoryForCryptogram(cryptogram). It seems that the web service is capable sending/receiving cryptograms only, not the solutions history
	*  same for addCryptogramAttempt(cryptogramAttempt). As per my interpretation of the Reqs, attempts are stored in a local database. ExternalWebService is not capable of accepting attempts.
> generatePlayerId is an inferred method used tackle the case of username collision across multiple applications. It is not specified but it will be required for proper function. 
> I think I probably over-generalized here. Technically, getAllCryptograms() and getHistoryForCryptogram() is all an application would need to implement getPlayerRatings(), per our discussion easier. However, an explicit method would probably be better.
> The addCryptogramAttempt is necessary to update player rankings globally. Again, per my thoughts about how to implement player rankings, this is all we need. 

1.  CryptogramAttempt class
	*  submitAnswer may also be a responsibility of Player (minor), but should return an indication of a success/failure as per Req#10.
	*  it seems we interpret Req10-c differently. 
	> Assign (or reassign) replacement letters to the encrypted letters and view the effects of these assignments in terms of resulting potential solution
	
	Does CryptogramAttempt::computeCryptogram(attempt) compute an encrypted version of player's answer so that he can compare the result of encryption of his potential solution with the current cryptogram?
	
	I took this Req as a UI feature [like this](http://www.cryptograms.org/retry2.php?ti=44181088) which does not imply/call any methods at all. 
	
	*  If ExternalWebService is not able to store solutions to the remote server (and user ratings are just aggregate stats over all cryptograms), then CryptogramAttempt should have properties to keep track of submission attempts and recently submitted solution phrase by the user

> SubmitAnswer could definitely go either way. From my perspective, the success or failure indication is an implementation detail - the CRD primarily defines how classes talk to one another, and should not get too bogged down in implementation details. 
> I took ComputeCryptogram to mean essentially "recompute the players answer per stored cipher".

1.	Player
	*  viewPlayerRatings() - if these ratings are not requested via ExternalWebService, but computed based on CryptogramAttempt, then CryptogramAttempt should have an additional property to hold #submissions/#incorrect_submissions made by a particular user on a particular cryptogram. Or viewPlayerRatings() request ratings from the remote server as per Req#5-d? There is some ambiguity to me
	> Actually, each new attempt of a cryptogram is a single submission, so the #correct/incorrect submissions is the number of cryptogramAttempts present. I wonder if that jives with the workflow, though.
	
1.  Administrator class
	*  In fact, the Reqs are not clear about whether the administrator should specify any cipher setting when creating the cryptogram or just use some pre-configured algorithm. Req#9 does not state that the administrator may choose the encryption algorithm (hence, provide a cipher in addition to encodedPhrase and solution).
	> I agree. My thinking was that it's likely the administrator would determine, for example, the shift # or substitution, and a cipher utility would compute its impact on their solution to generate the "encoded" phrase.
	
1.  User
	* The purpose of the role attribute is not explained
	> Good catch. The Role attribute defines whether the user is a player or administrator. I don't know that it's strictly necessary, and we could probably remove it.

	
### Overall

I think, our designs have much in common. The major points of disagreement are
*	Functionality provided by ExternalWebService 
*	Treatment of UserRatings (computed based on CryptogramAttempt persistent info vs requested from the remote server)
*	Hence, the list of properties of CryptogramAttempt

