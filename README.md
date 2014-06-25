MozillaRecovery
===============

Recovers the master password of key3.db files, i.e. Thunderbird, Firefox

Delevelopment is discontinued for the moment. Have a look at this fork of MozillaRecovery, which is actively maintained:
https://github.com/gtfy/MozillaRecovery

### Usage

Once you start the program, it searches automatically for default locations of your key3.db in Firefox and, if not found, in the Thunderbird application directory. I prepared and tested this for Windows 7 and Linux. You can change the location manually, of course. key3.db is the file that is used to recover the master password. You can start a wordlist attack on that. 

Alternatively you can generate the words by activating the "generate words" checkbox. Although I used threads, word generation is limited to a word length of five (a bruteforce attack with a wordlength of six would take several days, so I don't allow that) and the alphabet a-zA-Z by now.
