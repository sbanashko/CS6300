# Requirements list 

## 1. A user shall be able to choose to log in as the administrator or as a specific player when starting the application.  For simplicity, authentication is optional.
To implement this requirement, I created classes User, Player, and Administrator. Player and Administrator are subclasses of User. 
A user who chooses not to log in as an administrator is always considered a Player, with all attributes blank.

## 2. The application shall allow players to  (1) choose a cryptogram to solve, (2) solve cryptograms, (3) see previously solved cryptograms, and (4) view the list of player ratings.
To implement this requirement, I created classes Cryptogram and CryptogramAttempt, added method getPlayerRating to Utiltiy ExternalWebService, and added method viewSolvedCryptograms to class Player.

## 3. The application shall allow the administrator to (1) add new cryptograms, and (2) add new local players.
To implement this requirement, I added methods createUser and createCryptogram to class Administrator. 

## 4. The application shall maintain an underlying database to save persistent information across runs (e.g., cryptograms, players, solutions).
This requirement is not represented in the design, as all instances of all classes are persisted. 

## 5. Cryptograms and player ratings will be shared with other instances of the application.  An external web service utility will be used by the application to communicate with a central server to:
To implement this requirement, I added the submitAnswer method to class CryptogramAttempt, and created a dependency between the Administrator class and the ExternalWebService.
This dependency represents registering a new cryptogram with the ExternalWebService, and also with informing the ExternalWebService of a new user.

## a. Send updated player ratings.
To implement this requirement, I added a dependency between CryptogramAttempt and ExternalWebService. This represents the additional operation of invoking ExternalWebService.addCryptogramAttempt 
when invoking submitAnswer. 

## b. Send new cryptograms and receive a unique identifier for them.
The dependency between Administrator and ExternalWebService implements this requirement, which uses the method addCryptogram(). I also added Utility class UUIDGeneratorService. 
UUIDGeneratorService also serves the addUser method. 

## c. Request a list of additional cryptograms.
To implement this requirement, I added method "getAvailableCryptograms" to utility ExternalWebService. 
This method is invoked by the player when calling viewCryptogramList() - if available, the server will first send an updated list of cryptograms. 

## d. Request the current player ratings.
To implement this requirement, I added method "getPlayerRatings" to utility ExternalWebService. It is invoked by the method viewPlayerRatings() on class Player.

## You should represent this utility as a utility class called "ExternalWebService" that (1) is connected to the other classes in the system that use it and (2) explicitly lists relevant methods used by those classes.
To implement this requirement, I made ExternalWebService a utility class.

## 6. A cryptogram shall have an encoded phrase (encoded with a simple substitution cipher), and a solution. 
To implement this requirement, I added the attributes "encodedPhrase" and "solution" to the Cryptogram class.

## 7. A cryptogram shall only encode alphabetic characters, but it may include other characters (such as punctuation, numbers, or white spaces).
This requirement suggested to me that cryptograms would be generated in a consistent fashion, and so I created the utility class CipherService, which is used by the Administrator class.

## 8. To add a player, the administrator will enter the following player information:
## a. A first name
## b. A last name
## c. A unique username
These requirements were implemented by adding the relevant attributes to the Player class - however, the username attribute must be common to the administrator and the player 
(the administrator's username is, for example, "administrator") so the username attribute was added to the User superclass.
As an aside, because in order to have player rankings and cryptogram attempts sorted correctly by player, players must have a unique identifier registered with the server,
there is an additional dependency on ExternalWebService when adding a player. 


## 9. To add a new cryptogram, an administrator will:
## a. Enter a solution phrase.
## b. Enter a matching encoded phrase.
## c. Edit any of the above information as necessary.
## d. Save the complete cryptogram.
## After doing so, the administrator shall see a confirmation message. The message shall contain the unique identifier assigned to the cryptogram by the external web service utility.
These requirements did not impact my design, as all of them were achieved by the previous cryptogram class design or are specific workflow details.

## 10. To choose and solve a cryptogram, a player will:
## a. Choose a cryptogram from a list of all available cryptograms (see also Requirement 11).
These requirements did not impact my design, as they are  specific workflow details.

## b. View the chosen cryptogram (including any prior solution, complete or in progress, in case he or she already worked on the same cryptogram earlier).
This requirement suggested to me that a cryptogram should be aware of all of its previous attempts, and so I created the aggregation relationship between Cryptogram and CryptogramAttempt.
I also added the attribute "status" and "submission" to the class CryptogramAttempt, reqpresenting whether the cryptogram attempt is submitted (incorrectly solved), solved (correctly), or in progress, and representing 
the submitted solution.

## c. Assign (or reassign) replacement letters to the encrypted letters and view the effects of these assignments in terms of resulting potential solution.
This requirement caused me to add the method "computeCryptogram" to the class CryptogramAttempt, which will cause it to use its aggregating class (Cryptogram) to invoke CipherService.applyCipherToString()

## d. Submit the current solution when he or she has replaced all letters in the puzzle and is satisfied with such solution.
This requirement caused me to add submitAnswer to the class CryptogramAttempt. 

## At this point, the player shall get a result indicating whether the solution was correct. At any point, the player may return to the list of cryptograms to try another one.
This requirement did not impact my design, as it is a specific workflow detail. 

## 11. The list of available cryptograms shall show, for each cryptogram, its identifier, whether the player has solved it, and the number of incorrect solution submissions, if any.
This requirement impacted how I envisioned a CryptogramAttempt - specifically, as a link between a Cryptogram and a Player. Concretely, I added the method "viewHistoryForCryptogram" to 
class "Player." 

## 12. The list of player ratings shall display, for each player, his or her name, the number of cryptograms solved, the number of cryptograms started, and the total number of incorrect solutions submitted. The list shall be sorted in descending order by the number of cryptograms solved.  
Because of the way I designed CryptogramAttempt, no additional design considerations were required to achieve this requirement. A player rating is simply an ordered list of players, which contain references to 
CryptogramAttempts. 

## 13. The User Interface (UI) shall be intuitive and responsive.
This requirement did not impact my design, as it is vague and generalized.

# Other thoughts
The essential design is this: A Cryptogram contains its cipher, solution, and encoded phrase. When a player "attempts" a cipher, they generate and remember a new instance of CryptogramAttempt.
CryptogramAttempts submit themselves to the ExternalWebService, and so a player does not need to know about the service in order to simply play the game. The only time the player must 
interact with this service is to view player rankings. Since each player knows about its own attempts, and each attempt knows about its own cryptogram, rankings can be computed per cryptogram 
without the need to maintain a separate class for rankings. 
