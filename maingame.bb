Function maingamesetup()
	;If usertype="client"
		
		;If firsttimethrough=False
		;	For player.player=Each player
		;		player\cube=CreateCube()
		;	Next
		
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
		Else
			programlocation="maingame"
		EndIf
		
		If usertype="host"
			If startclockmillisecs=0
				startclockmillisecs=MilliSecs()
			EndIf
			
			For player.player=Each player
				;If player\username<>myusername
					WriteString(lanstream,"001"+(MilliSecs()-startclockmillisecs))
					SendUDPMsg(lanstream,player\ip,player\port)
				;EndIf
			Next
		EndIf
		
		While RecvUDPMsg(lanstream)
			readstringstream$=ReadString(lanstream)
			typeofmsg$=Mid$(readstringstream,1,3)
			
			If typeofmsg="001"
				startclockmillisecs=Mid$(2,Len(readstringstream)-3)
				;DebugLog readstringstream
				;DebugLog startclockmillisecs
			EndIf
			
		Wend
	;EndIf
	
	UpdateWorld()
	RenderWorld()
	
	Flip
End Function
		

Function maingame()
	maingamemessages()
	maingameinputs()
	maingamecollisions()
	maingame3d()
	maingamehud()
	
	Flip
End Function

Function maingamemessages()
End Function

Function maingamecollisions()
End Function

Function maingame3d()
UpdateWorld
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