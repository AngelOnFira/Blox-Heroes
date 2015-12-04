Function maingamesetup()
	;If usertype="client"
		
		;If firsttimethrough=False
		;	For player.player=Each player
		;		player\cube=CreateCube()
		;	Next
		
		;If we are still hiding the map
		If preparemap=False
			If nowshowmap=False
				If hidemap()
					nowshowmap=True
				EndIf
			EndIf
			
			If nowshowmap And preparemap=False
				If revealmap() Then preparemap=True
				;Stop
			EndIf
		;Otherwise go to the main game loop
		Else
			programlocation="maingame"
			i=0
			For player.player = Each player
				EntityType player\cube, 2;player\collisionType
				;i=i+1
			Next
			
			;For j=0 To i-1
			;	For k=j+1 To i
			;		DebugLog "i; "+i+", j; "+j+", k; "+k
			;		Collisions j, k, 3, 2
			;	Next
			;Next
			
			;Stop
			ClearCollisions
			Collisions 2,2,3,2
			Return
		EndIf
		
		;If it's a host
		If usertype="host"
			If startclockmillisecs=0
				startclockmillisecs=MilliSecs()
			EndIf
			
			;Sync timer?
			For player.player=Each player
				;If player\username<>myusername
					WriteString(lanstream,"001"+(MilliSecs()-startclockmillisecs))
					SendUDPMsg(lanstream,player\ip,player\port)
				;EndIf
			Next
		EndIf
		
		;Update sync timer?
		While RecvUDPMsg(lanstream)
			readstringstream$=ReadString(lanstream)
			typeofmsg$=Mid$(readstringstream,1,3)
			
			If typeofmsg="001"
				startclockmillisecs=Mid$(2,Len(readstringstream)-3)
			EndIf
			
		Wend	
	UpdateWorld()
	RenderWorld()
	
	Flip
End Function
		

Function maingame()
	Cls
	maingamemessages()
	maingameinputs()
	UpdateWorld
	RenderWorld
	maingamecollisions()
	;maingame3d()
	maingamehud()
	
	Flip
End Function

Function maingamemessages()
	checkmessages(lanstream)
End Function

Function maingamecollisions()
	If usertype="host"
		For playerOne.player = Each player
			entityCollision=EntityCollided(playerOne\cube, 2)
			If entityCollision
				For playerTwo.player = Each player
					If playerTwo\cube=entityCollision; And playerOne\username<>playerTwo\username
						For playerInfo.player = Each player
							WriteString(lanstream,"017"+LSet$(playerOne\username,30)+""+LSet$(playerTwo\username,30))
							SendUDPMsg(lanstream,playerInfo\ip,playerInfo\port)
						Next
						;Text 0,0, "Collided with player "+player\username
					EndIf
				Next
			EndIf
		Next
	EndIf
End Function

Function maingame3d()
	RenderWorld
End Function

Function maingamehud()
	MasterFPSCounter()
End Function

Function maingameinputs()
	If typingprogress
		If typing()
			FlushKeys()
			typingprogress=False
			If usertype="client"
				hosts.host=First host
				WriteString(lanstream,"011"+typingmessage)
				SendUDPMsg lanstream,hosts\ip,hosts\port
				chatbox.chatbox=New chatbox
				chatbox\message=typingmessage
				chatbox\ip=myip("IIP")
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
End Function