Function maingamesetup()	
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
				EntityType player\cube, 2
			Next
			
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
	UpdateWorld()
	maingamecollisions()
	maingame3d()
	maingamehud()
	
	Flip
End Function

Function maingamemessages()
	checkmessages(lanstream)
End Function

Function maingamecollisions()
	If usertype="host"
		;Check and see if each player has collided
		
		i=0
		For playerOne.player = Each player
			i=i+1
		Next
		
		playerOne.player = First player
		For j = 0 To i-1
			entityCollision=EntityCollided(playerOne\cube, 2)
			If entityCollision
				;Check if this was the player that he collided with
				playerTwo.player = Last player
				For k = j+1 To i
					If playerTwo\cube=entityCollision
						If playerOne\flag Or playerTwo\flag
							random1=Rnd(1, numOfSpawns-1)
							
							random2=0
						;Make sure random 2 isn't the same as random one
							Repeat
								random2=Rnd(1, numOfSpawns-1)
							Until random2<>random1
							
						;Find the first spawn location
							findSpawn1.spawn = First spawn
							For i=1 To random1
								findSpawn1 = After findSpawn1
							Next
							
						;Find the second spawn location
							findSpawn2.spawn = First spawn
							For i=1 To random2
								findSpawn2 = After findSpawn2
							Next
							
						;Move the players on the hosts screen
							For player.player = Each player
								If player\username = playerOne\username
									PositionEntity player\cube,findSpawn1\dimX,findSpawn1\dimY,0
								ElseIf player\username = playerTwo\username
									PositionEntity player\cube,findSpawn2\dimX,findSpawn2\dimY,0
								EndIf
							Next
							
							If playerOne\flag = True
								playerOne\flag = False
								playerTwo\flag = True
							Else
								playerTwo\flag = False
								playerOne\flag = True
							EndIf
							
						;Send the new player locations to other players
							For playerInfo.player = Each player
								If playerInfo\username<>myusername
									WriteString(lanstream,"017"+LSet$(playerOne\username,30)+""+LSet$(playerTwo\username,30)+""+LSet$(random1, 3)+""+LSet$(random2, 3))
									SendUDPMsg(lanstream,playerInfo\ip,playerInfo\port)
								EndIf
							Next
						EndIf
					EndIf
					
					playerTwo = Before playerTwo
				Next
			EndIf
			
			playerOne = After playerOne
		Next
	EndIf
End Function

Function maingame3d()
	For player.player = Each player
		If player\flag
			EntityColor player\cube,255,0,0
		Else
			EntityColor player\cube,255,255,255
		EndIf
	Next
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
		;Get movement info
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
		
		;Send all players movement info
		If usertype="host"
			For playersend.player=Each player
				If playersend\port<>UDPStreamPort(lanstream)
					WriteString(lanstream,"012"+currentkeycode)
					SendUDPMsg(lanstream,playersend\ip,playersend\port)
				EndIf
			Next
		EndIf
		
		;Send the host the movement info
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
;~IDEal Editor Parameters:
;~C#Blitz3D