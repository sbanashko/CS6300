1. A user shall be able to choose to log in as the administrator or as a specific player when starting the application.  For simplicity, authentication is optional.
    To realize this requirement I added to the design a LocalDataService class.  This class is responsible for communicating with the local database which holds user information.  This class has a login method which checks the credentials entered by the user against the local database and provides the associated User class back to the application.  The user class has an IsAdmin attribute which determines whether the user has administrator privileges.
2. The application shall allow players to  (1) choose a cryptogram to solve, (2) solve cryptograms, (3) see previously solved cryptograms, and (4) view the list of player ratings.
    Points 1 and 2 are realized by requirement 10.  To realize point 3 I added the method SeeSolved() to the LocalDataService classes which makes use of the Solved attribute of the Cryptogram class and displays the cryptograms which have the Solved attribute set to True.  Part 4 is realized in requirement 5.d solution.
3. The application shall allow the administrator to (1) add new cryptograms, and (2) add new local players.
    Point 1 is realized in requirement 9.  Point 2 is realized in requirement 8.
4. The application shall maintain an underlying database to save persistent information across runs (e.g., cryptograms, players, solutions).
    In order to satisfy this requirement I add the LocalDataService class.  This class has cryptograms (which have solutions) and players classes as attributes.  This class is the single interface between the application and the local database.
5. Cryptograms and player ratings will be shared with other instances of the application.  An external web service utility will be used by the application to communicate with a central server to:
    I added to the design a utility class named ExternalWebService which is to act as a means of communication with the world outside the application.
    a. Send updated player ratings.
        I added to the ExternalWebService class a method SendPlayerRatings() which sends the local player ratings to the external server.
    b. Send new cryptograms and receive a unique identifier for them.
        I added to the ExternalWebService class a method CreateCryptogram().  This method is called by the LocalDataService class when a user with administrator privileges is adding a new cryptogram.  The method returns a unique id created by the remote server which is associted with the new cryptogram and stored in the local database.
    c. Request a list of additional cryptograms.
        On the ExternalWebService class I added a method FetchCryptograms() which is called by the LocalDataService class when a player is selecting to view cryptograms.  The FetchCryptograms() method returns a list of new cryptograms which are then added to the local database.
    d. Request the current player ratings.
        To meet this I added a method RequestRatings() to the ExternalWebService class.  This method fetches the current player ratings from a remote server.  It is called by the LocalDataService upon a user requesting them.
6. A cryptogram shall have an encoded phrase (encoded with a simple substitution cipher), and a solution. 
    To realize this requirement I created a class Cryptogram which has EncodedPhrase and Solution as attributes.
7. A cryptogram shall only encode alphabetic characters, but it may include other characters (such as punctuation, numbers, or white spaces).
    In order to realize this requirement I added the attribute ValidCharacters to the Cryptogram class so that during creation there is a set of characters against which to check.
8. To add a player, the administrator will enter the following player information:
    a. A first name
    b. A last name
    c. A unique username
    To realize this requirement I created a user class with attributes FirstName, LastName, and UserName.
9. To add a new cryptogram, an administrator will:
    a. Enter a solution phrase.
    b. Enter a matching encoded phrase.
    c. Edit any of the above information as necessary.
    d. Save the complete cryptogram.    
    After doing so, the administrator shall see a confirmation message. The message shall contain the unique identifier assigned to the cryptogram by the external web service utility.
    To realize sub points a & b of this requirement I created the class Cryptogram with attributes Solution and EncodedPhrase.  For point c I choose not to model this within the class diagram but it is part of the process of adding a new cryptogram.  For point d I added the method AddCryptogram() to the LocalDataService class.  Upon calling this, the LocalDataService class will in turn call the CreateCryptogram method on the ExternalWebService class. The AddCryptogram() method will return to the Administrator the unique id for the cryptogram that was assigned by the remote server.
10. To choose and solve a cryptogram, a player will:
    a. Choose a cryptogram from a list of all available cryptograms (see also Requirement 11).
        To realize this point I added the method ViewCryptograms() to the LocalDataService class which will display a list of available cryptograms.  Upon selecting one to view the ChooseCryptogram() method is called which exists on the LocalDataService class.
    b. View the chosen cryptogram (including any prior solution, complete or in progress, in case he or she already worked on the same cryptogram earlier).
        As mentioned above the ChooseCryptogram() method will select the cryptogram from a list for the user to view.  On the cryptogram class I added the attributes Solution, Solved, and CurrentProgress.  The CurrentProgress attribute contains the latest state of any work being done on the Cryptogram by the User.  If the Solved attribute is set to True, then the user may also view the Solution.
    c. Assign (or reassign) replacement letters to the encrypted letters and view the effects of these assignments in terms of resulting potential solution.
        On the Cryptogram class I added the method Encode().  This method is assigns an individual replacement letter in the cryptogram.
    d. Submit the current solution when he or she has replaced all letters in the puzzle and is satisfied with such solution.
        To recognize this point I added the method Solve() to the Cryptogram class which checks the solution against the valid solution.
    At this point, the player shall get a result indicating whether the solution was correct. At any point, the player may return to the list of cryptograms to try another one.
    To realize the first part of this The Solve() method shall return the result of comparing the user's solution to the valid one present on the Cryptogram class.  The latter part is realized by the ViewCryptograms() method on the LocalDataService class.
11. The list of available cryptograms shall show, for each cryptogram, its identifier, whether the player has solved it, and the number of incorrect solution submissions, if any.
    To realize this requirement I added to the Cryptogram class the attributes UniqueID, Solved, and WrongAttempts.
12. The list of player ratings shall display, for each player, his or her name, the number of cryptograms solved, the number of cryptograms started, and the total number of incorrect solutions submitted. The list shall be sorted in descending order by the number of cryptograms solved.  
    To realize this requirement I created an association class called Rating which contains the attributes Name, NumSolved, NumStarted, TotalWrong where each instance of Rating is associated with a single User class.  The NumSolved attribute can be used by the UI for sorting.
13. The User Interface (UI) shall be intuitive and responsive.
    I chose not to implement this requirement in the class diagram because it does not directly associate with any data types.