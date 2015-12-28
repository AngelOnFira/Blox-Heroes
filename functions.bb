;Setup the graphics
Function setgraphics()
	graphicsfile=OpenFile("System/Settings.blx")
	
	If graphicsfile=0
		If FileType("System")=0
			CreateDir CurrentDir$+"System"
		EndIf
		
		gp=WriteFile("System/Settings.blx")
		WriteLine(gp,"Width=800")
		WriteLine(gp,"Height=600")
		WriteLine(gp,"Color Depth=16")
		WriteLine(gp,"Video mode=2")
		
		For i=1 To CountGfxModes3D()
			If GfxModeWidth(i)=800 And GfxModeHeight(i)=600 And GfxModeDepth(i)=16
				WriteLine(gp,i)
			EndIf
		Next
		
		;For i=1 To CountGfxModes3D()
		;	WriteLine(gp,GfxModeWidth(i))
		;Next
		
		CloseFile gp	
	EndIf
	
	graphicsproperties=OpenFile("System/Settings.blx")
	
	tempwidth$=ReadLine(graphicsproperties)
	programwidth=Mid(tempwidth,7,Len(tempwidth)-5)
	
	tempheight$=ReadLine(graphicsproperties)
	programheight=Mid(tempheight,8,Len(tempheight)-6)
	
	tempcolordepth$=ReadLine(graphicsproperties)
	programcolordepth=Mid(tempcolordepth,13,Len(tempcolordepth)-11)
	
	tempvideomode$=ReadLine(graphicsproperties)
	programvideomode=Mid(tempvideomode,12,Len(tempvideomode)-10)
	
	notconstvidmode=ReadLine(graphicsproperties)	
	
	Graphics3D programwidth,programheight,programcolordepth,programvideomode

End Function

;Find the graphics config file
Function checkfolders()
	
	thisdir=ReadDir(CurrentDir())
	
	Repeat
		file=NextFile(thisdir)
		
		If file="" Then Exit
		
		gf.graphicsfile = New graphicsfile
		gf\name=file
		gf\filefolder=FileType(file)
	Forever
End Function

;Function to initialize game
Function initialize()
	setgraphics()
	readplayerinfo()
	loadgamevitals()
	load3dworld()
	loadsounds()
End Function

;Read the saved player info
Function readplayerinfo()
	playerinfofile=OpenFile("System/playerinfo.txt")
	If playerinfofile=0
		playerinfofile=WriteFile("System/playerinfo.txt")
		WriteString(playerinfofile,"Number of profiles=1")
		WriteString(playerinfofile,"Username="+Input("Please enter a profile name;"))
		;WriteLine(playerinfofile,"Password="encryptpassword()
	EndIf
	
	crap=ReadLine(playerinfofile)
	username=ReadLine(playerinfofile)
End Function	
	
;Load logo and other pics
Function loadgamevitals()
	imagebutton=LoadImage("Media/button.png")
	MaskImage imagebutton,0,255,0
	
	imagegametitle=LoadImage("Media/Blox Heros.png")
	MaskImage imagegametitle,0,0,0
	ScaleImage imagegametitle,.8,.8
	
	textfont=LoadFont("Arial",24)
End Function

;Setup the light for 3D
Function load3dworld()
	light=CreateLight()
End Function

;Load sounds for the game
Function loadsounds()

End Function

;Start the main menu
Function mainmenu()
	SetFont textfont
	Cls
	drawmainmenu()
	playerinfo()
	
	If checkinputsmainmenu()
		Return
	EndIf
	
	Flip
End Function

;Draw the menu
Function drawmainmenu()
	DrawImage imagebutton,programwidth/15,programheight/20
	DrawImage imagebutton,programwidth/15,programheight/8.57
	DrawImage imagebutton,programwidth/15,programheight/5.45
	DrawImage imagebutton,programwidth/15,programheight/4
	
	DrawImage imagegametitle,programwidth/2,programheight/300
	
	Color 240,20,20
	Text programwidth/3.94,programheight/13.3,"Start",1,1
	Text programwidth/3.94,programheight/7.05,"Settings",1,1
	Text programwidth/3.94,programheight/4.8,"Credits",1,1
	Text programwidth/3.94,programheight/3.64,"Exit",1,1
	Color 255,255,255

End Function

;Check for inputs in the menu
Function checkinputsmainmenu()
	If MouseHit(1)
		x=MouseX()
		y=MouseY()
		
		If x>53 And x<353 And y>30 And y<60
			programlocation="startmenu"
			Return True
		EndIf
		
		If x>53 And x<353 And y>70 And y<100
			programlocation="settings"
		EndIf
		
		If x>53 And x<353 And y>110 And y<140
			
		EndIf
		
		
		If x>53 And x<353 And y>150 And y<180
			End
		EndIf

	EndIf
	If KeyHit(2);1
		programlocation="setuplangame"
		showmap=True
	EndIf
	
	If KeyHit(3);2
		showmap=True
		setupLanSearch()
		usertype="client"
		myusername="AngelOnFira"
		startnetwork()
	EndIf
	
	If KeyHit(1) Then End
End Function

;Second menu, start menu
Function startmenu()
		Cls
		drawstartmenu()
		playerinfo()
		checkinputsstartmenu()
		Flip
End Function

;Draw the start menu
Function drawstartmenu()
	DrawImage imagebutton,programwidth/15,programheight/20
	DrawImage imagebutton,programwidth/15,programheight/8.57
	DrawImage imagebutton,programwidth/15,programheight/5.45
	DrawImage imagebutton,programwidth/15,programheight/4
	DrawImage imagebutton,programwidth/15,programheight/3.16
	
	DrawImage imagegametitle,programwidth/2,programheight/300
	
	Color 240,40,10
	Text programwidth/3.94,programheight/13.3,"Start Local Game",1,1
	Text programwidth/3.94,programheight/7.05,"Search Online Servers",1,1
	Text programwidth/3.94,programheight/4.8,"Search Lan Servers",1,1
	Text programwidth/3.94,programheight/3.64,"Host Game",1,1
	Text programwidth/3.94,programheight/2.93,"Back",1,1
End Function

;Check for inputs in the start menu
Function checkinputsstartmenu()
	If MouseHit(1)
		x=MouseX()
		y=MouseY()
		
		;Go back to the start menu?
		If x>53 And x<353 And y>30 And y<60
			startmenu()
		EndIf
		
		;Nothing yet
		If x>53 And x<353 And y>70 And y<100
			
		EndIf
		
		;Search for lan game
		If x>53 And x<353 And y>110 And y<140
			setupLanSearch()
			usertype="client"
			myusername="AngelOnFira"
			startnetwork()
		EndIf
		
		;Host a game
		If x>53 And x<353 And y>150 And y<180
			programlocation="hostmenu"
		EndIf
		
		;Return to the main menu
		If x>53 And x<353 And y>190 And y<220
			programlocation="mainmenu"
		EndIf
	EndIf
	
	;Esc goes to main menu
	If KeyHit(1)
		programlocation="mainmenu"
	EndIf
End Function

;Settings menu
Function settings()
	readsettings()
	Cls
	drawsettingmenu()
	checkinputssettingsmenu()
	Flip
End Function

;Scalable settings menu
Function drawsettingmenu()
	ScaleImage imagebutton,1,2
	DrawImage imagebutton,programwidth/15,programheight/2
	ScaleImage imagebutton,1,.5
	DrawImage imagebutton,programwidth/15,programheight/8.57
	DrawImage imagebutton,programwidth/15,programheight/5.45
	DrawImage imagebutton,programwidth/15,programheight/4
	DrawImage imagebutton,programwidth/15,programheight/3.16
	
	;DrawImage imagegametitle,programwidth/2,programheight/300
	
	Color 240,40,10
	Text programwidth/3.94,programheight/13.3,"Resolution; "+settingwidth+"x"+settingheight,1,1
	
	Text programwidth/3.94,programheight/7.05,"Color Depth",1,1
	
	Select settingvideomode
		Case 1
			Text programwidth/3.94,programheight/4.8,"Full Screen",1,1
		Case 2
			Text programwidth/3.94,programheight/4.8,"Windowed",1,1
	End Select

	Text programwidth/3.94,programheight/3.64,"Host Game",1,1
	Text programwidth/3.94,programheight/2.93,"Back",1,1
End Function

Function readsettings()
	graphicsproperties=OpenFile("System/Settings.blx")
	
	tempwidth$=ReadLine(graphicsproperties)
	settingwidth=Mid(tempwidth,7,Len(tempwidth)-5)
	
	tempheight$=ReadLine(graphicsproperties)
	settingheight=Mid(tempheight,8,Len(tempheight)-6)
	
	tempcolordepth$=ReadLine(graphicsproperties)
	settingcolordepth=Mid(tempcolordepth,13,Len(tempcolordepth)-11)
	
	tempvideomode$=ReadLine(graphicsproperties)
	settingvideomode=Mid(tempvideomode,12,Len(tempvideomode)-10)
	
End Function

Function checkinputssettingsmenu()
	If MouseHit(1)
		x=MouseX()
		y=MouseY()
		
		If x>53 And x<353 And y>30 And y<60
			Repeat
				Cls
				;For i=1 To CountGfxModes()
					drawsettingmenu()
					Text programwidth/2,programheight/20,"Hi"
					Text programwidth/2,programheight/12,"Hi"
					Text programwidth/2,programheight/8.6,"Hi"
					Flip
			Forever
		EndIf
		
		If x>53 And x<353 And y>70 And y<100
			
		EndIf
		
		If x>53 And x<353 And y>110 And y<140
			
		EndIf
		
		If x>53 And x<353 And y>150 And y<180
			End
		EndIf

		If x>53 And x<353 And y>190 And y<220
			programlocation="mainmenu"
		EndIf
	EndIf
End Function

Function hostmenu()
		Cls
			drawhostmenu()
			checkinputshostmenu()
		Flip
End Function

Function drawhostmenu()
	DrawImage imagebutton,programwidth/15,programheight/20
	DrawImage imagebutton,programwidth/15,programheight/8.57
	DrawImage imagebutton,programwidth/15,programheight/5.45
	;DrawImage imagebutton,programwidth/15,programheight/4
	;DrawImage imagebutton,programwidth/15,programheight/3.16
	
	DrawImage imagegametitle,programwidth/2,programheight/300
	
	Color 240,40,10
	Text programwidth/3.94,programheight/13.3,myip$("DottedIP"),1,1
	Text programwidth/3.94,programheight/7.05,"Host Game",1,1
	Text programwidth/3.94,programheight/4.8,"Back",1,1
	;Text programwidth/3.94,programheight/3.64,"Host Game",1,1
	;Text programwidth/3.94,programheight/2.93,"Back",1,1
	
	Text 0,0,MouseX()
	Text 0,30,MouseY()
	
	Flip
End Function

Function checkinputshostmenu()
	If MouseHit(1)
		x=MouseX()
		y=MouseY()
		
		If x>53 And x<353 And y>30 And y<60
			programlocation="startmenu"
		EndIf
		
		If x>53 And x<353 And y>70 And y<100
			programlocation="setuplangame"
			showmap=True
		EndIf
		
		If x>53 And x<353 And y>110 And y<140
			programlocation="startmenu"
		EndIf
		
		If x>53 And x<353 And y>150 And y<180
			End
		EndIf

		
	EndIf
End Function

Function setuplangame()
	Print "setuplangame"
	usertype="host"
	startnetwork()
	programlocation="runhostlanmenu"
End Function

Function runhostlanmenu()
	player.player=New player
	player\username="HostUserName"
	player\flag = True
	player\ip=myip("IIP")
	player\port=2200
	player\cube=CreateCube()
	ScaleEntity player\cube,.99,.99,1.01
	PositionEntity player\cube,11,11,0
	myusername="HostUserName"
	playercolour(myusername)
	player\collisionType=0
	currentPlayerType=1
	
	hostcamera=CreateCamera(player\cube)
	PositionEntity hostcamera,0,-20,-15
	RotateEntity hostcamera,-70,0,0
	
	hostmap="test.bhlvl"
	loadmap(hostmap)
	programlocation="lobby"
	FlushKeys()
End Function

;asdfasdfasdfasdf
;asfdasfasdfasdf
;adsfasdfasdfasdf
;asdfasdfasfdasdf;as
;fdsafasfasdf
;fdsafasdfasdf

Function drawhostlanmenu()
	Cls
	i=24
	For player.player=Each player
		Text 0,i,player\username
		i=i+24
	Next
	
	Text 0,0,"Draw host lan menu"
	Flip
End Function

Function runclientlanmenu()
	drawlansearch()
	checkinputslansearch()
	If checkmessages(lanstream)			
		programlocation="lobby"
		FlushKeys()
	EndIf
	checkpings()
End Function

Function drawlansearch()
	Cls
	
	If MilliSecs()-lastradar>2000
		FreeEntity radar
		radar=CreateCube()
		PositionEntity radar,EntityX(lansearchplayercube),EntityY(lansearchplayercube),EntityZ(lansearchplayercube)
		EntityAlpha radar,.3
		radaralpha=.3
		EntityColor radar,0,255,0
		radarscale=1
		lastradar=MilliSecs()
		WriteString(lanstream,"002")
		SendUDPMsg lanstream,IIPtoDIP("172.17.73.16"),2200;IIPtoDIP(findhosts()),2200
	ElseIf radar<>0
		radarscale=(radarscale+.5)
		ScaleEntity radar,radarscale,radarscale,radarscale
		If radaralpha>=.1
			radaralpha=radaralpha-.01
		ElseIf radaralpha<.1
			radaralpha=radaralpha-.005
		EndIf
		EntityAlpha radar,radaralpha
	EndIf
	
	i=0
	For hosts.host=Each host
		If hosts\lastmove=0 Then hosts\lastmove=hosts\averageping
		
		movehowfar=(hosts\averageping-hosts\lastmove)
		If movehowfar<1000
			MoveEntity hosts\cube,0,movehowfar,0
		EndIf
		hosts\lastmove=hosts\averageping
	Next

	UpdateWorld()
	
	;If EntityCollided(lansearchplayercube,1)
		For hosts.host=Each host
			If hosts\cube=EntityCollided(lansearchplayercube,1)
				WriteString(lanstream,"004"+LSet$(usernamebuffed,30))
				SendUDPMsg lanstream,hosts\ip,hosts\port
			EndIf
		Next
	;EndIf
	RenderWorld()
	
	Text 0,50, myip("DottedIP")
	
	For hosts.host=Each host
		Text 0,i*20,hosts\ip
		i=i+1
	Next
	;Test loop time should be around 17 on laptop	
	Flip
End Function

Function checkinputslansearch()	
	If KeyDown(17);W
		MoveEntity lansearchplayercube,0,.3,0
	EndIf
	
	If KeyDown(30);A
		TurnEntity lansearchplayercube,0,0,4
	EndIf
	
	If KeyDown(31);S
		MoveEntity lansearchplayercube,0,-.3,0
	EndIf
	
	If KeyDown(32);D
		TurnEntity lansearchplayercube,0,0,-4
	EndIf
	RotateEntity lansearchplayercube,0,0,Int(EntityRoll(lansearchplayercube))

End Function

Function checkpings()
	;For each host if we haven't checked ping for 200ms, check ping
	For hosts.host=Each host
		If MilliSecs()-hosts\lastcheck>200
			hosts\lastcheck=MilliSecs()
			WriteString(lanstream,"006")
			SendUDPMsg lanstream,hosts\ip,hosts\port
		EndIf
		
		;If the host hasen't been around for 4 seconds, delete it
		If MilliSecs()-hosts\lastrecieve>4000
			Delete hosts
		EndIf
	Next
End Function

Function lobby()
		If usertype="host"
			checkmessages(lanstream)
			For players.player=Each player
				;If the player is not me
				If players\port<>UDPStreamPort(lanstream)
					;If we haven't checked with the player in 3 seconds
					If players\lastcheck<(MilliSecs()-3000)
						;If we haven't recieved anything for 6 seconds
						If players\lastrecieve<(MilliSecs()-6000)
							;Delete the player
							FreeEntity players\cube
							Delete players
						Else
							;Update the players check information
							players\lastcheck=MilliSecs()
							
							;Send them a message and see if they are still there
							WriteString(lanstream,"013")
							SendUDPMsg(lanstream,players\ip,players\port)
						EndIf
					EndIf
				EndIf
			Next
		EndIf
		
		If usertype="client"
			checkmessages(lanstream)
			lobbychecks()
			checkpings()
		EndIf
		
		If MilliSecs()-looptime>20
			checkinputslobby()
			looptime=MilliSecs()
		EndIf
		
		If showmap
			revealmap()
		ElseIf erasemap
			hidemap()
		;ElseIf mapdistance=100
		;	mapdistance=0
		EndIf
		
		drawlobby()
End Function

Function drawlobby()
	Cls
	i=24
	
	UpdateWorld()
	RenderWorld()
	
	For player.player=Each player
		Text 0,i,player\username
		i=i+24
	Next
	
	Text 200,0,myip("IIP")
	
	Text 0,0,"Draw host lan menu"
	If usertype="host"
		Text 0,100,"Host"
	EndIf
	
	If typingprogress
		Text 200,100,typingmessage
		i=Len(typingmessage)
		
		If i=0
			i=10
		Else
			i=i*12
		EndIf
		
		Rect 200,100,i,24,0
	EndIf
	
	numchats=0
	For chatbox.chatbox=Each chatbox
		numchats=numchats+1
	Next
	
	If numchats>5
		numchats=5
	EndIf
	
	chatbox.chatbox=Last chatbox
	For i=1 To numchats
		Text 200,100+(20*i),chatbox\message
		chatbox=Before chatbox
	Next
	;TurnEntity hostcamera,1,1,1
	Flip
End Function

Function checkinputslobby()
	If typingprogress
		If typing()
			FlushKeys()
			typingprogress=False
			If usertype="client"
				;Send the typed message to the host
				hosts.host=First host
				WriteString(lanstream,"011"+typingmessage)
				SendUDPMsg lanstream,hosts\ip,hosts\port
				
				;Add it to the current chat box
				chatbox.chatbox=New chatbox
				chatbox\message=typingmessage
				chatbox\ip=myip("IIP")
				
			;If it is the host, send the message to all of the players
			ElseIf usertype="host"
				For player.player=Each player
					If player\username<>myusername
						WriteString(lanstream,"015"+LSet(myip("IIP"),11)+""+typingmessage)
						SendUDPMsg lanstream,player\ip,player\port
					EndIf
				Next
				chatbox.chatbox=New chatbox
				chatbox\message=typingmessage
				chatbox\ip=myip("IIP")
			EndIf
			typingmessage=""
		EndIf
	Else
		currentkeycode=""
		For player.player=Each player
			If player\username=myusername
				If KeyDown(17);W
					currentkeycode=currentkeycode+"1"
					MoveEntity player\cube,0,.3,0
				Else
					currentkeycode=currentkeycode+"0"
				EndIf
				
				If KeyDown(30);A
					currentkeycode=currentkeycode+"1"
					TurnEntity player\cube,0,0,4
				Else
					currentkeycode=currentkeycode+"0"
				EndIf
				
				If KeyDown(31);S
					currentkeycode=currentkeycode+"1"
					MoveEntity player\cube,0,-.3,0
				Else
					currentkeycode=currentkeycode+"0"
				EndIf
				
				If KeyDown(32);D
					currentkeycode=currentkeycode+"1"
					TurnEntity player\cube,0,0,-4
				Else
					currentkeycode=currentkeycode+"0"
				EndIf
				RotateEntity player\cube,0,0,Int(EntityRoll(player\cube))
			EndIf
		Next
		
		If usertype="host"
			For playersend.player=Each player
				If playersend\port<>UDPStreamPort(lanstream)
					WriteString(lanstream,"012"+currentkeycode)
					SendUDPMsg(lanstream,playersend\ip,playersend\port)
				EndIf
			Next
		EndIf
		
		If usertype="client"
				WriteString(lanstream,"008"+currentkeycode)
				hosts.host=First host
				SendUDPMsg lanstream,hosts\ip,hosts\port
		EndIf
		
		If KeyHit(28)
			typingprogress=True
			FlushKeys()
		EndIf
	EndIf
		
	If KeyHit(1)
		;Leaving the game
		If usertype="client"
			;Delete the map
			For map.map=Each map
				FreeEntity map\cube
				Delete map
			Next
			
			;Tell the server that you are leaving
			WriteString(lanstream,"010")
			hosts.host=First host
			SendUDPMsg lanstream,hosts\ip,hosts\port
			
			;Delete the host
			For hosts.host=Each host
				Delete hosts
			Next
			
			;Delete each other player
			For player.player=Each player
				Delete player
			Next
			
			;Clean up
			CloseUDPStream(lanstream)
			usertype=""
			myusername=""
			setupLanSearch()
		EndIf
		
		If usertype="host"
			For map.map=Each map
				FreeEntity map\cube
				Delete map
			Next
			For player.player=Each player
				FreeEntity player\cube
				Delete player
			Next
			CloseUDPStream(lanstream)
			usertype=""
			myusername=""
			hostmap=""
			programlocation="mainmenu"
		EndIf
	EndIf
End Function

Function lobbychecks()
	hosts.host=First host
	If hosts\lastrecieve<MilliSecs()-6000
		Delete hosts
		Cls
		Locate 0,0
		Print "Connection to host was lost, press any key to continue"
		WaitKey()
		Locate 0,0
				
		For player.player=Each player
			FreeEntity player\cube
			Delete player
		Next
		
		CloseUDPStream lanstream
		setupLanSearch()
	EndIf
End Function

Function checklanmessages(stream)
	If usertype="client"
		While RecvUDPMsg(stream)
			readstringstream$=ReadString(stream)
			typeofmsg$=Mid$(readstringstream,1,3)
			
		Wend
	EndIf
		
End Function

Function startnetwork()
	;Create the host
	If usertype="host"
		lanstream=CreateUDPStream(2200)
		
	;Create the client
	ElseIf usertype="client"
		lanstream=CreateUDPStream(2201)
		WriteString(lanstream,"002")
		SendUDPMsg lanstream,IIPtoDIP(findhosts()),2200
		
		lastradar=MilliSecs()
		lansearchplayercube=CreateCube()
		lansearchcamera=CreateCamera(lansearchplayercube)
		
		EntityColor lansearchplayercube,255,0,0
		EntityType lansearchplayercube,2
		PositionEntity lansearchcamera,0,-20,-15
		RotateEntity lansearchcamera,-70,0,0
	EndIf
End Function

Function myip$(typeip$)
	hosts=CountHostIPs("")
	myip=HostIP(1)
	If typeip="IIP"
		;Return the internal IP
		Return myip
	Else If typeip="DottedIP"
		;Return the dotted IP
		Return DottedIP$(myip)
	EndIf
End Function

Function IIPtoDIP(ip$)
	;Convert internal IP to dotted IP
	hosts=CountHostIPs(ip)
	Return HostIP(hosts)
End Function

Function playerinfo()
	Text 0,0,Mid(username,10,Len(username)-10)
End Function

Function loadmap(mapname$)
	mapfile=OpenFile("maps\Blox Heros\"+mapname)
	dimensions$=ReadLine(mapfile)
	dimenx=Trim(Mid(dimensions,1,3))
	dimeny=Trim(Mid(dimensions,3,3))
	numOfSpawns=0
	
	For i=1 To dimeny
		mapline$=ReadLine(mapfile)
		For j=1 To dimenx
			If Mid(mapline,j,1)=1
				map.map=New map
				map\unittype=1
				map\cube=CreateCube()
				HideEntity map\cube
				PositionEntity map\cube,j*2,i*2,0
				EntityType map\cube,1
			ElseIf Mid(mapline,j,1)=2
				newSpawn.spawn = New spawn
				newSpawn\dimX=j*2
				newSpawn\dimY=i*2
				numOfSpawns=numOfSpawns+1
			EndIf
		Next
	Next
End Function

Function playercolour(playerusername$)
	For player.player=Each player
		If player\username=playerusername
			totalcolour=200
			firstcolour=Rnd(100,180)
			totalcolour=totalcolour-firstcolour
			secondcolour=Rnd(0,totalcolour)
			thirdcolour=totalcolour-secondcolour
			num=Rnd(1,6)
			Select num
				Case 1
					player\red=firstcolour
					player\green=secondcolour
					player\blue=thirdcolour
				Case 2
					player\green=firstcolour
					player\red=secondcolour
					player\blue=thirdcolour
				Case 3
					player\blue=firstcolour
					player\green=secondcolour
					player\red=thirdcolour
				Case 4
					player\red=firstcolour
					player\blue=secondcolour
					player\green=thirdcolour
				Case 5
					player\blue=firstcolour
					player\red=secondcolour
					player\green=thirdcolour
				Case 6
					player\green=firstcolour
					player\blue=secondcolour
					player\red=thirdcolour
			End Select
			EntityColor player\cube,player\red,player\green,player\blue
		EndIf
	Next
End Function

Function findhosts$()
	mydottedip$=myip("DottedIP")
	netip$=""
	Repeat
		If Not Mid$(mydottedip,i+1,1)="."
			netip=netip+""+Mid$(mydottedip,i+1,1)
		ElseIf Mid$(mydottedip,i+1,1)="." And numdots<>2
			netip=netip+"."
			numdots=numdots+1
		Else
			netip=netip+".255"
			Return netip$
		EndIf
		i=i+1
	Forever
End Function

Function revealmap()
	If mapdistance=0
		maxdistancemap()
	EndIf

	For player.player=Each player
		If player\username=myusername
			For map.map=Each map
				If Sqr(((EntityX(map\cube)-EntityX(player\cube))^2)+((EntityY(map\cube)-EntityY(player\cube))^2))<mapdistance
					ShowEntity map\cube
				EndIf
			Next
		EndIf
	Next
	mapdistance=mapdistance+1;.1
	If mapdistance>=maxmapdistance
		maxmapdistance=0
		mapdistance=0
		showmap=False
		Return True
	EndIf
End Function

Function hidemap()
	If mapdistance=0
		maxdistancemap()
		mapdistance=maxmapdistance
	EndIf
	
	For map.map=Each map
		If Sqr((EntityX(map\cube)-hideoriginx)^2+(EntityY(map\cube)-hideoriginy)^2)>mapdistance
			HideEntity map\cube
		EndIf
	Next
	mapdistance=mapdistance-1;.1
	If mapdistance<=0
		mapdistance=0
		Return True
	EndIf
End Function

Function typing()
	key=GetKey()
	If key
		If key=8
			typingmessage=Left(typingmessage,Len(typingmessage)-1)
		ElseIf key=13
			If usertype="host" And typingmessage="start"
				findmapdistance=True
				typingprogress=False
				FlushKeys()
				For player.player=Each player
					If player\username=myusername
						hideoriginx=EntityX(player\cube)
						hideoriginy=EntityY(player\cube)
					EndIf
				Next
				programlocation="maingamesetup"
				For player.player=Each player
					If player\username<>myusername
						WriteString lanstream,"016"
						SendUDPMsg(lanstream,player\ip,player\port)
					EndIf
				Next
			Else
				Return True
			EndIf
		Else
			typingmessage=typingmessage+Chr$(key)
		EndIf
	EndIf
End Function

Function maxdistancemap()
	mapdistance=0
	For player.player=Each player
		If player\username=myusername
			For map.map=Each map
				If Sqr((EntityX(map\cube)-EntityX(player\cube))^2+(EntityY(map\cube)-EntityY(player\cube))^2)>maxmapdistance
					maxmapdistance=Sqr((EntityX(map\cube)-hideoriginx)^2+(EntityY(map\cube)-hideoriginy)^2)+3
				EndIf
			Next
		EndIf
	Next
End Function
	
Function masterFPSCounter()
	If MilliSecs() - lastFPSCheck < 1000
		totalFPSChecks = totalFPSChecks + 1
	Else
		lastFPSCheck=MilliSecs()
		FPSAmount=totalFPSChecks
		totalFPSChecks = 0
	EndIf
	Color 255,255,255
	Text 100,100,FPSAmount
End Function

Function setupLanSearch()
	Collisions 2,1,3,2
	programlocation="runclientlanmenu"
End Function
	
;~IDEal Editor Parameters:
;~C#Blitz3D