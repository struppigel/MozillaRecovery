MozillaRecovery
===============

Recovers the master password of key3.db files, i.e. Thunderbird, Firefox

### Usage

Once you start the program, it searches automatically for default locations of your key3.db in Firefox and, if not found, in the Thunderbird application directory. I prepared and tested this for Windows 7 and Linux. If it is not working for your OS, please tell me the default location for it. I just need the information to put that in. You can change the location by hand, of course.

key3.db is the file that is used to recover the master password. You can start a wordlist attack on that. The program ships with a default worldlist, but it is small (I didn't want to upload a wordlist file that adds several megabytes to the program). You can use your own list by changing the location.

Alternatively you can generate the words by activating the "generate words" checkbox. Although I used threads, word generation is limited to a word length of five (a bruteforce attack with a wordlength of six would take several days, so I don't allow that) and the alphabet a-zA-Z by now. I got about 30000 password tests per second on my machine.

Once you got the master password, it is very easy to obtain saved login information from signons.sqlite, since both, Thunderbird and Firefox, will show usernames and passwords in plain text. (google if you don't know how)

Conclusion: Always set a master password if you save login information with Thunderbird or Firefox. Otherwise the login information can be obtained without any problems.
