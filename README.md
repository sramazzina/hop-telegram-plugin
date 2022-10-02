# Apache Hop Telegram plugin

## Overview

[Telegram](https://telegram.org/) is the famous open source messaging platform used by a wide set of users worldwide. Ths plugin gives you the capability to have Hop interacting with a user by going through Telegram. 

* The first and very basic feature is the ability to send a message to a Telegram channel by using a transform or an action to inform a set of user that something hapened during the execution of a pipeline or a workflow.
* The second and more advanced feature is the ability to use Hop as a backend of a Telegram Bot that, by using a set of pipelines to build a custom made logic, is able to interact with a user and answer to user's commands or questions.  

## Build

### Pre-requisites for building the project:
* Maven, version 3+
* Java JDK 11

### How to compile and build the project
From the main directory, or any of the module sub-directories, run the following command

`mvn clean package`.

### Installation

After building or downloading from the releases, you can copy the zip file into Hop's root directory and unzip it and the restart Hop.

## Usage documentation
TBD

## Other
### Support
This plugin is provided “as is”, without any warranties, expressed or implied. This software is not covered by any Support Agreement.

### License
Licensed under the Apache License, Version 2.0.