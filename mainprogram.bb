SeedRnd MilliSecs()
;All Globals;
Global testnum
;--------;
;Graphics;
;--------;

Global programwidth#,programheight#,programcolordepth#,programvideomode#
Global notconstvidmode#

;-----------;
;Check Files;
;-----------;
Type graphicsfile
	Field name
	Field filefolder
End Type

;----------------;
;Load game vitals;
;----------------;

Global imagebutton,imagegametitle
Global textfont

;--------;
;Settings;
;--------;

Global myusername$,programlocation$
Global settingwidth,settingheight,settingcolordepth,settingvideomode
Global lastFPSCheck#, totalFPSChecks, FPSAmount

;-------;
;Network;
;-------;

Global lanstream,joiningstream,usertype$
Global testtimer,clicktimer,lastplace,place=1
Global firstping,timerepeat
Global currentkeycode$
Global usernamebuffed$="AngelOnFira",usernames$
Global hostcamera,hostmap$,lastradar,radarscale#,radaralpha#,radar
Global lansearchplayercube
;----------------------;
;Other files to include;
;----------------------;

Include "functions.bb"
Include "maingame.bb"
Include "checkmessages.bb"

initialize()
programlocation="mainmenu"

Repeat
	If programlocation="mainmenu"
		mainmenu()
	ElseIf programlocation="startmenu"
		startmenu()
	ElseIf programlocation="settings"
		settings()
	ElseIf programlocation="setuplangame"
		setuplangame()
	;ElseIf programlocation="setup"
	ElseIf programlocation="runclientlanmenu"
		runclientlanmenu()
	ElseIf programlocation="hostmenu"
		hostmenu()
	ElseIf programlocation="runhostlanmenu"
		runhostlanmenu()
	ElseIf programlocation="lobby"
		lobby()
	ElseIf programlocation="maingamesetup"
		maingamesetup()
	ElseIf programlocation="maingame"
		maingame()
	EndIf

Forever

;setupgame()

;---------;
;Main Game;
;---------;
Type player
	Field ip$, port$
	Field username$
	Field x,y,z,rotation,cube
	Field lastcheck,lastrecieve
	Field red,green,blue
	;Field (STATES)
End Type

Type host
	Field gamename$, gamemap$, cube, lastmove
	Field ip, port
	Field lastcheck, lastrecieve, ping, numpings, averagepings[20], averageping
End Type

Global lanplayersearchcube

;-------:
;Typeing;
;-------;
Global typingmessage$,typingprogress

;---;
;Map;
;---;

Type map
	Field cube,x,y,unittype
	Field dimx,dimy
End Type

Global showmap,hidemap,findmapdistance
Global mapdistance#, maxmapdistance
Global hideoriginx#,hideoriginy#

;--------;
;Chat Box:
;--------;

Type chatbox
	Field message$,ip,typeofchatmsg
End Type

;---------------;
;Main Game Setup;
;---------------;

Global startclockmillisecs%
Global nowshowmap, preparemap
;notes
;Type;
	;1-Wall
	;2-Player
	;3-Special