##### 1. A user shall be able to choose to log in as the administrator or as a specific player when starting the application.  For simplicity, authentication is optional.
User class has been added to the class diagram. Player and Administrator classes are also added to the class diagram which are a generalization of the User class. I'm going with the simple scenario of no authentication and hence moving the userName only to the Player class.


##### 2. The application shall allow players to  
##### (1) choose a cryptogram to solve,
##### (2) solve cryptograms, 
##### (3) see previously solved cryptograms, 
##### (4) view the list of player ratings.
To realize this requirement, Player class has been equipeed with chooseCryptogramToSolve(), solveCryptogram(), viewAlreadySolvedCryptograms() and viewPlayerRatings() methods. Player class is a derived class of the base User class.


##### 3. The application shall allow the administrator to 
##### (1) add new cryptograms, 
##### (2) add new local players.
For addressing this requirement, Administrator class has been equipped with addNewCryptogram() and addNewLocalPlayer() methods. Administrator class is a derived class of the base User class.


##### 4. The application shall maintain an underlying database to save persistent information across runs (e.g., cryptograms, players, solutions).
No database has been shown in the class diagram. Its assumed that the information shown in Cryptogram, Players and Solutions are stored in the database(as suggested in the Assignment).


##### 5. Cryptograms and player ratings will be shared with other instances of the application.  An external web service utility will be used by the application to communicate with a central server to:
##### a. Send updated player ratings.
##### b. Send new cryptograms and receive a unique identifier for them.
##### c. Request a list of additional cryptograms.
##### d. Request the current player ratings.
##### You should represent this utility as a utility class called "ExternalWebService" that (1) is connected to the other classes in the system that use it and (2) explicitly lists relevant methods used by those classes.
Administrator class uses the ExternalWebService in order to add a new cryptogram using the sendNewCryptogramReceiveUniqueId() method. Player class uses the ExternalWebService in order to view Player Ratings and choose cryptograms to solve and also to populate the current player's rating.


##### 6. A cryptogram shall have an encoded phrase (encoded with a simple substitution cipher), and a solution. 
To realize this requirement, I added to the design Cryptogram class with attributes encodedPhrase and solution:


##### 7. A cryptogram shall only encode alphabetic characters, but it may include other characters (such as punctuation, numbers, or white spaces).
This requirement is ignored as it does not affect the class diagram. This details the logic followed by the solveCryptogram() method of the Player class.
    

##### 8. To add a player, the administrator will enter the following player information:
##### a. A first name
##### b. A last name
##### c. A unique username
Only part of this requirement can be incorporated in the class diagram. In the User class, attributes firstName, lastName and username have been added. Administrator will be able to add the new player using addNewLocalPlayer() method available in the Administrator class.


##### 9. To add a new cryptogram, an administrator will:
##### a. Enter a solution phrase.
##### b. Enter a matching encoded phrase.
##### c. Edit any of the above information as necessary.
##### d. Save the complete cryptogram.
#####  After doing so, the administrator shall see a confirmation message. The message shall contain the unique identifier assigned to the cryptogram by the external web service utility.
Administrator class has been connected to the ExternalWebService as it uses the sendNewCryptogramReceiveUniqueId() method for adding a new Cryptogram. Also Administrator class has an association with the Cryptogram class, as it creates an object of type Cryptogram having the attributes solution and encodedPhrase. Also having access to this class, allows the Administrator to edit solution and encodedPhrase attributes.
    

##### 10. To choose and solve a cryptogram, a player will:
##### a. Choose a cryptogram from a list of all available cryptograms (see also Requirement 11).
##### b. View the chosen cryptogram (including any prior solution, complete or in progress, in case he or she already worked on the same cryptogram earlier).
##### c. Assign (or reassign) replacement letters to the encrypted letters and view the effects of these assignments in terms of resulting potential solution.
##### d. Submit the current solution when he or she has replaced all letters in the puzzle and is satisfied with such solution.   
##### At this point, the player shall get a result indicating whether the solution was correct. At any point, the player may return to the list of cryptograms to try another one.
Only a part of this requirement can be captured in the class diagram. Player can view any existing attempts for the Cryptogram using the Solution Class via viewAlreadySolvedCryptograms. Other requirements mentioned here are implementation details.
    

##### 11. The list of available cryptograms shall show, for each cryptogram, its identifier, whether the player has solved it, and the number of incorrect solution submissions, if any.
We've added a Solution class assuming that the Utility class only provides the uniqueIdentifiers for the Cryptograms. Solution class stores the player's userName and cryptogram's uniqueIdentifier as attributes along with the playerHasSolved and numberOfIncorrectSubmissions attributes. Player class can access these details from the Solution class.


##### 12. The list of player ratings shall display, for each player, his or her name, the number of cryptograms solved, the number of cryptograms started, and the total number of incorrect solutions submitted. The list shall be sorted in descending order by the number of cryptograms solved.  
Since player ratings is assumed to be provided by the ExternalWebService, nothing special has been done to address this requirement. In case, External service wants to get these details, Solution class can be used to retrieve the same.

##### 13. The User Interface (UI) shall be intuitive and responsive.
I did not consider this requirement because it does not affect the design directly.

