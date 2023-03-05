# jIRCd
jIRCd is an easy-to-use non-blocking high performance IRC server written in Java.

**This is a work in progress** and acts as an example of how to use the [Socket-Server](https://github.com/Konloch/Socket-Server/) library.

## How To Use
+ Start by [downloading the latest release](https://github.com/Konloch/jIRCd/releases), then you can launch the IRCd using the Java -jar flag.
```
java -jar jIRCd-v0.1.0.jar
```
+ This will create the default config under `./config.ini`
+ Adjust the configuration as needed, then restart the server.

## Implementation Progress
+ `IRC v1` is the target but certain `IRC v3 extensions` are planned to be supported ([SASL](https://ircv3.net/specs/extensions/sasl-3.1))
+ Configuration is done and supports variables
+ Core protocol decoding and encoding is done
+ Core API has been started and is partially finished
+ Temporary channels are done but persistent channels with permissions have not been started
+ NickServ / authorization has been started
+ User modes aren't started
+ Channel modes aren't started
+ Plugins / event system has been started

## Links
* [Website](https://konloch.com/jIRCd/)
* [Discord Server](https://discord.gg/aexsYpfMEf)
* [Download Releases](https://github.com/Konloch/jIRCd/releases)