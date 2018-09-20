## Notes

**My apologize for distracting watermarks all over the design.pdf. This is an unexpected side-effect of exporting from Visual Paradigm trial version**

1. Working on this assignment I tried _not_ to complicate the design by making unnecessary assumptions in cases the requirements were underspecified and also tried to avoid any implementation-specific details

1. Despite this is a distributed app (a network game or so), the design considers/models a single instance of the application, where the application (aka system) itself is _not_ explicitly modeled. Only the internal structure of the app/system is represented in the class diagram.

1. Getter/setter methods & constructors were not specifically modeled unless they explicitly follow from the requirements or add extra clarity (from my point of view)

1. Methods/attributes visibility was not paid much attention at this (high) level of specification; the same is true for datatypes designation for some of the attributes/parameters

1. Besides ExternalWebService class that encapsulates the details of interaction with an external server, I introduced similar concepts of _PlayerRepository_, _CryptogramRepository_ and _PotentialSolutionRepository_ that serve as proxies for underlying database entities and, thus, conveniently hide database interaction details from business domain entities. Their names _xxxRepository_ stem from the [repository design pattern](https://martinfowler.com/eaaCatalog/repository.html) applied


## Requirements

> 1. A user shall be able to choose to log in as the administrator or as a specific player when starting the application. For simplicity, authentication is optional.

To realize this requirement, I introduced two subclasses of a _User_ class - _LocalPlayer_ and _Administrator_. Since authentication facilities are optional (and can be supplied e.g. by the framework) they are not explicitly modeled. A class hierarchy with a _User_ superclass is actually redundant - in the design it mainly serves to emphasize that there are two distinct types of app users exhibiting different properties and behavior.

---

> 2. The application shall allow players to  (1) choose a cryptogram to solve, (2) solve cryptograms, (3) see previously solved cryptograms, and (4) view the list of player ratings.

These are quite general requirements, detailed through the subsequent requirements below. At this point I introduced a _LocalPlayer_ class, a _Cryptogram_ class, the "chooses" association between the two, a _PotentialSolution_ association class, repository classes, as well as some properties and methods of the aforementioned classes discussed below. 
 
---

> 3. The application shall allow the administrator to (1) add new cryptograms, and (2) add new local players.

The fact that the _Administrator_ role is expected to create new cryptograms and players is shown by the corresponding methods of that class. These methods, however, delegate the actual work to _addPlayer_ and _addCryptogram_ methods (potentially allowing for additional checks) of the corresponding repository classes (note "call" dependencies between _Administrator_ and _xxxRepository_). _Administrator_ class attributes are left unspecified at this point.

---

> 4. The application shall maintain an underlying database to save persistent information across runs (e.g., cryptograms, players, solutions).

System-specific entities (like Application, Database, UI widgets) were _not_ modeled as being implementation/framework-specific (to a larger extent, I believe). Instead, I employed _xxxRepository_ wrappers mentioned above for _LocalPlayer, PotentialSolution, Cryptogram_ classes to allow state preservation across application runs.

---

> 5. Cryptograms and player ratings will be shared with other instances of the application.  An external web service utility will be used by the application to communicate with a central server to:
>   * Send updated player ratings.
>   * Send new cryptograms and receive a unique identifier for them.
>   * Request a list of additional cryptograms.
>   * Request the current player ratings.

The requirement is represented by a utility class _ExternalWebService_ with four methods - _sendCryptogram, requestPlayerRatings, requestCryptograms, sendPlayerRatings_ - invoked by _LocalPlayer_ (I do not like this dependency, though the thought behind it was to allow for immediate ratings update upon cryptogram completion) and _CryptogramRepository_ methods as shown with dependency relationships between these classes.  

There is also a helper structure _PlayerRatings_ the only purpose of which is to store values of several user ratings (i.e. the number of cryptograms solved, started, incorrect solutions submitted).

The utility class _UniqueIdentifier_ represents the universal notion of "unique identifiers" assigned to cryptograms (e.g. GUID-like, unique sequential integers, etc.).

Lastly, _requestCryptograms_ returns "a list of additional cryptograms" shown as Collection\<Cryptogram\> return type, again, just to abstract from a concrete generic.

---

> 6. A cryptogram shall have an encoded phrase (encoded with a simple substitution cipher), and a solution.

There is an _encodedPhrase_ field and _encode_ method in a _Cryptogram_ class that accepts a phrase (a solution), encrypts it without touching _encodedPhrase_ and returns an encoded phrase based on current encryption settings. A _SimpleSubstitutionCipher_ class is a blueprint intended to store those encryption settings (methods allowing to configure them are omitted for now).

---

> 7. A cryptogram shall only encode alphabetic characters, but it may include other characters (such as punctuation, numbers, or white spaces)

This requirement has to do with implementation of the _Cryptogram_ class/_encode_ method. I tend to think it does not affect the _design_ (at least in a form it has to be presented in the assignment).

---

> 8. To add a player, the administrator will enter the following player information:
>  - A first name
>  - A last name
>  - A unique username

This requirement is realized by the _addPlayer_ method of the _Administrator_ class (delegating to _PlayerRepository::addPlayer_), the properties of the _LocalPlayer_ class and _PlayerRepository::isUnique_ helper to ensure the uniqueness constraint against local player names stored in the database.

---

> 9. To add a new cryptogram, an administrator will:
>  - Enter a solution phrase.
>  - Enter a matching encoded phrase.
>  - Edit any of the above information as necessary.
>  - Save the complete cryptogram.

> After doing so, the administrator shall see a confirmation message. The message shall contain the unique identifier assigned to the cryptogram by the external web service utility.

There is an _addCryptogram_ method in the _Administrator_ class. _CryptogramRepository::addCryptogram_ is used to add new cryptograms to the database. 
Both methods accept a solution and encoded phrases as parameters. _Cryptogram::encode_ can be used to obtain an encoded phrase from the supplied solution phrase so the caller can ensure that entered solution and encoded phrases  match each other (e.g. after editing). Based on the encoded and solution phrases a new _Cryptogram_ can be instantiated via a constructor (shown as new). _ExternalWebService_ method _sendCryptogram_ is called by _CryptogramRepository::addCryptogram_ (note a "call" dependency between classes) to submit a newly created cryptogram to the remote server, receive its unique identifier and set _Cryptogram_'s _UID_ field. There is also a _getUID_ method to retrieve UID for a confirmation message.

---

> 10. To choose and solve a cryptogram, a player will:
>  - Choose a cryptogram from a list of all available cryptograms (see also Requirement 11).
>  - View the chosen cryptogram (including any prior solution, complete or in progress, in case he or she already worked on the same cryptogram earlier).
>  - Assign (or reassign) replacement letters to the encrypted letters and view the effects of these assignments in terms of resulting potential solution.
>  - Submit the current solution when he or she has replaced all letters in the puzzle and is satisfied with such solution.

> At this point, the player shall get a result indicating whether the solution was correct. At any point, the player may return to the list of cryptograms to try another one.

The list of all all available cryptograms is returned by _getCryptograms_ method of _CryptogramRepository_ proxy class. _getCryptograms_ shall also request additional cryptograms from the remote server via _requestCryptograms_ based on some business rule. This will happen transparently for the caller. The design just states a dependency between these utility classes. The fact that a player may choose a cryptogram from a list to start working with is represented by an association between _LocalPlayer_ and a _Cryptogram_ and an association role named _chosenCryptogram_.

_getEncodedPhrase_ allows the user to view a chosen cryptogram. The notion of prior solutions is modeled by an association class _PotentialSolution_, the _LocalPlayer_ has access to. _LocalPlayer::getPriorSolution_ returns a prior solution (if any) of the current user and chosen cryptogram pair. By querying the fields of _PotentialSolution_ object (getters not shown) the caller should be able to retrieve the solution phrase and the completion status of the solution.

The process of working on the cryptogram does not affect the design (presumably handled by the UI components).

Submission of the solution is accomplished by _LocalPlayer::submitSolution_ method that delegates the responsibility to verify the proposed solution to the _Cryptogram's_ class _verifySolution_ method and returns a boolean indication of solution correctness. _PotentialSolution_ submission counters, flags and the solution phrase get updated at this point to properly reflect the current status of the cryptogram the user just has submitted. _LocalPlayer::updateRaings_ is responsible for passing the updated statistics to the remote server via _ExternalWebService::sendPlayerRatings_ (note a "call" dependency). There is also a _getRatings_ method in _LocalPlayer_, the _updateRaings_ may wish to use in order to merge new statistics with previously known ratings before sending an update to the remote server. 

Lastly, _saveSolution_ method allows to save the solution currently in progress without submitting it or affecting its status in any way.

---

> 11. The list of available cryptograms shall show, for each cryptogram, its identifier, whether the player has solved it, and the number of incorrect solution submissions, if any.

The list of available cryptograms can be obtained through _CryptogramRepository_, whereas the properties of each list item (a cryptogram instance) will be supplied by an appropriate _Cryptogram_ method. 

*  _getUID_ - returns cryptogram unique identifier 
*  _isSolvedBy_ - returns boolean flag if the player has solved this cryptogram 
*  _getIncorrectSolutionsSubmittedCount_ - returns the number of incorrect solution submissions for the player 

There is also a dependency from the _PotentialSolutionRepository_ that can query local database to obtain these statistics.

---

> 12. The list of player ratings shall display, for each player, his or her name, the number of cryptograms solved, the number of cryptograms started, and the total number of incorrect solutions submitted. The list shall be sorted in descending order by the number of cryptograms solved.

Likewise Req#11, a list of player ratings (presumably, _local players'_ ratings as can be inferred from the fact that only _current player_ ratings can be requested from the remote server, whereas only a local player may ever be current) is represented by a collection of _LocalPlayer_ instances returned by _PlayerRepository::getPlayers_. For each player in this collection ratings are returned in a _PlayerRatings_ structure by _getRatings_ method of _LocalPlayer_ class. _getRatings_, in turn, may request ratings from the remove server through _requestPlayerRatings_ method of _ExternalWebService_ utility class (note a "call" dependency). 

Lastly, the sorting order requirement for the player ratings list is an implementation detail and is not shown in the design (can be handled by a UI component holding the list or the concrete data structure that represents the collection).

---

> 13. The User Interface (UI) shall be intuitive and responsive.

This is a non-functional requirement and does not affect the design directly
