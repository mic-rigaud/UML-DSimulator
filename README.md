# UML-DSimulator

## This Repositories

This repositories contain all my project

## Requirement

This plugin need Java 8

Linux:

    sudo apt-get install openjdk-8-jre openjdk-8-jdk openjdk-8-jre-headless

Windows and Mac, download on this page:

[Java SE Development Kit 8 - Downloads](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

## Description of the project

UML-DSimulator is a plugin for UML Designer. This plugin is a simulator of UML Model. The plugin use a simulator which was developed by [Ciprian Teodorov](https://github.com/teodorov).

With this simulator it is possible to see the evolution of States in all State machine diagram.

## Installation in UML Designer

After installing the Java 8, Copy Paste the jar files in the UML Designer folder name plugins.

Then restart UML Designer.

To add the view go on Windows/Show View/Other/Simulator/UML-DSimulator

## Folder contain

### uml-plugin

Folder with the eclipse development environment.

To run eclipse:

    ./uml-plugin/eclipse/eclipse

Sources of the plugin are on the subfolder *ws*

### Documentation

In this folder there is a report here: [report](Documentation/report/rapport_de_base.pdf)

This report contains the explanation of every think about my work.

There is also the final presentation of my work

### Deployment

This folder contains what is needed to install the plugin on a computer.

### SCCD

This folder contains the transformer for SCCD. The SCCD software can be found on the MSDL git server
[simon/SCCD: The SCCD (Statecharts + Class Diagrams) compiler and runtime. - MSDL Git server](https://msdl.uantwerpen.be/git/simon/SCCD)

## Version

<table border="2" cellspacing="0" cellpadding="6" rules="groups" frame="hsides">


<colgroup>
<col  class="right" />

<col  class="left" />
</colgroup>
<thead>
<tr>
<th scope="col" class="right">Version</th>
<th scope="col" class="left">Stable</th>
</tr>
</thead>

<tbody>
<tr>
<td class="right">1.0</td>
<td class="left">Yes</td>
</tr>
</tbody>
</table>
