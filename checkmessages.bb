Function checkmessages(stream)
	While RecvUDPMsg(stream)
		readstringstream$=ReadString(stream)
		typeofmsg$=Mid$(readstringstream,1,3)
		;Host revieves message from client looking for a server
		If typeofmsg="002" And usertype="host"
			WriteString(stream,"003")
			WriteString(stream,hostmap)
			WriteString(stream,numofplayers)
			SendUDPMsg stream,UDPMsgIP(stream),UDPMsgPort(stream)
		EndIf
		;Client is notified that a server is there, and adds the server
		If typeofmsg="003"
			shouldadd=True
			For hosts.host=Each host
				If hosts\ip=UDPMsgIP(stream)
					shouldadd=False
				EndIf
			Next
			If shouldadd=True
				newhost.host=New host
				newhost\ip=UDPMsgIP(stream)
				newhost\port=UDPMsgPort(stream)
				newhost\gamemap=ReadString(stream)
				newhost\ping=MilliSecs()-firstping
				newhost\lastcheck=MilliSecs()
				newhost\lastrecieve=MilliSecs()
				newhost\cube=CreateCube()
				EntityType newhost\cube,1
				RotateEntity newhost\cube,0,0,Rnd(-180,180)
			EndIf
		EndIf
		;Ping test
		If typeofmsg="035"
			testtimer=MilliSecs()-testtimer
		EndIf
		
		;Host recieves message asking if player can join
		If typeofmsg="004"
			usernames=Mid$(readstringstream,4,30)
			usernames=Trim(usernames)
			
			addplayer=True
			For player.player=Each player
				If usernames=player\username
					addplayer=False
				EndIf
			Next
			
			If addplayer=True
				currentPlayerType=currentPlayerType+1
				
				newplayer.player=New player
				newplayer\ip=UDPMsgIP(stream)
				newplayer\port=UDPMsgPort(stream)
				newplayer\username=usernames
				newplayer\cube=CreateCube()
				ScaleEntity newplayer\cube,.99,.99,1.01
				newplayer\lastcheck=MilliSecs()
				newplayer\lastrecieve=MilliSecs()
				newplayer\collisionType=currentPlayerType
				
				playercolour(usernames)
							
				red=0
				green=0
				blue=0
				For player.player=Each player
					If player\username=usernames
						red=player\red
						green=player\green
						blue=player\blue
					EndIf
				Next
				
				WriteString(stream,"005")
				For players.player=Each player
					If players\username<>usernames
						WriteString(stream,LSet$(players\username,30)+""+LSet$(players\ip,11)+""+LSet$(players\port,5))
						WriteString(stream,LSet$(EntityX(players\cube),6)+""+LSet$(EntityY(players\cube),6))
						WriteString(stream,LSet$(EntityRoll(players\cube),6))
						WriteString(stream,LSet$(players\red,3)+""+LSet$(players\green,3)+""+LSet$(players\blue,3))
						WriteString(stream,LSet$(currentPlayerType,3))
					EndIf
				Next
				WriteString(stream,"End")
				WriteString(stream,LSet$(red,3)+""+LSet$(green,3)+""+LSet$(blue,3))
				SendUDPMsg stream,UDPMsgIP(stream),2201
				For players.player=Each player
					If players\ip<>UDPMsgIP(stream) And players\username<>"HostUserName"
						WriteString(stream,"051"+LSet$(usernames,30)+""+LSet$(UDPMsgIP(stream),13)+""+LSet$(UDPMsgPort(stream),5))
						SendUDPMsg stream,players\ip,players\port
					EndIf
				Next
			EndIf
		EndIf
		
		If typeofmsg="005"
		;Get all other players info
		;Stop
			Repeat
				textread$=ReadString(stream)
				If textread<>"End"
					newplayer.player=New player
					newplayer\username=Trim(Mid(textread,1,30))
					newplayer\ip=Trim(Mid(textread,31,11))
					newplayer\port=Trim(Mid(textread,42,5))
					newplayer\cube=CreateCube()
					ScaleEntity newplayer\cube,.99,.99,1.01
					
					textread$=ReadString(stream)
					PositionEntity newplayer\cube,Trim(Mid(textread,1,6)),Trim(Mid(textread,7,6)),0
					newplayer\x=Trim(Mid(textread,1,6))
					newplayer\y=Trim(Mid(textread,7,6))
					
					textread$=ReadString(stream)
					RotateEntity newplayer\cube,0,0,Trim(textread)
					newplayer\rotation=Trim(textread)
					
					textread$=ReadString(stream)
					newplayer\red=Mid(textread,1,3)
					newplayer\green=Mid(textread,4,3)
					newplayer\blue=Mid(textread,7,3)
					EntityColor newplayer\cube,newplayer\red,newplayer\green,newplayer\blue
					
					textread$=ReadString(stream)
					newplayer\collisionType=Trim(Mid(textread,1,3))
				EndIf
			Until textread="End"
			
			;Make myself a player
			player.player=New player
			player\username="AngelOnFira"
			player\ip=myip("IIP")
			player\port=2201
			player\cube=CreateCube()
			ScaleEntity player\cube,.99,.99,1.01
			
			clientcamera=CreateCamera(player\cube)
			PositionEntity clientcamera,0,-20,-15
			RotateEntity clientcamera,-70,0,0
			
			;Get my colour	
			textread$=ReadString(stream)
			For player.player=Each player
				If player\username=myusername
					player\red=Mid(textread,1,3)
					player\green=Mid(textread,4,3)
					player\blue=Mid(textread,7,3)
					EntityColor player\cube,player\red,player\green,player\blue
				EndIf
			Next
			
			;Delete hosts
			For hosts.host=Each host
				If hosts\ip<>UDPMsgIP(stream)
					FreeEntity hosts\cube
					Delete hosts
				Else
					FreeEntity hosts\cube
					FreeEntity lansearchplayercube
					FreeEntity radar
					loadmap(hosts\gamemap)
					hosts\lastrecieve=MilliSecs()
					hosts\lastcheck=MilliSecs()
				EndIf
			Next
			Return True
		EndIf
		
		;New player
		If typeofmsg="051"
			textread$=ReadString(stream)
			newplayer.player=New player
			newplayer\username=Trim(Mid(textread,1,30))
			newplayer\ip=Trim(Mid(textread,31,11))
			newplayer\port=Trim(Mid(textread,42,5))
			newplayer\cube=CreateCube()
		EndIf
		
		;Bounce back a ping request
		If typeofmsg="006"
			WriteString(stream,"007")
			SendUDPMsg stream,UDPMsgIP(stream),UDPMsgPort(stream)
		EndIf
		
		;Update that server is still there, and adjust their ping
		If typeofmsg="007"
			For hosts.host=Each host
				If hosts\ip=UDPMsgIP(stream) And hosts\port=UDPMsgPort(stream)
					hosts\ping=MilliSecs()-hosts\lastcheck
					hosts\lastcheck=MilliSecs()
					hosts\lastrecieve=MilliSecs()
					
					;Make an average ping
					If hosts\numpings <> 19
						hosts\averagepings[hosts\numpings]=hosts\ping
						hosts\numpings=hosts\numpings+1
					Else
						;Move the array up one for new ping values
						For i=0 To 19
							hosts\averagepings[i]=hosts\averagepings[i+1]
						Next
						hosts\averagepings[20]=hosts\ping
					EndIf
					
					;Make a new average
					hosts\averageping=0					
					For i=0 To hosts\numpings+1
						hosts\averageping=hosts\averageping+hosts\averagepings[i]
					Next
					hosts\averageping=(hosts\averageping)/(hosts\numpings)
				EndIf
			Next
		EndIf
		
		;Get movement info from the client and apply it to each player,
		;then send it to each player
		If typeofmsg="008"
			For players.player=Each player
				If players\port=UDPMsgPort(stream)
					If Mid(readstringstream,4,1)=1
						MoveEntity players\cube,0,.3,0
					EndIf
					If Mid(readstringstream,5,1)=1
						TurnEntity players\cube,0,0,4
					EndIf
					If Mid(readstringstream,6,1)=1
						MoveEntity players\cube,0,-.3,0
					EndIf
					If Mid(readstringstream,7,1)=1
						TurnEntity players\cube,0,0,-4
					EndIf
				EndIf
			Next
			
			;Send the info to each player	
			For playersend.player=Each player
				If playersend\ip<>UDPMsgIP(stream) And playersend\port<>2200
					WriteString(stream,"012"+Mid(readstringstream,3,4))
					WriteString(stream,UDPMsgIP(stream))
					SendUDPMsg(stream,playersend\ip,playersend\port)
				EndIf
			Next
		EndIf
		
		;Player is still in lobby
		If typeofmsg="009"
			For players.player=Each player
				If players\port=UDPMsgPort(stream)
					;Update the time that they said they were there
					players\lastrecieve=MilliSecs()
				EndIf
			Next
		EndIf
		
		;Player has said that he is leaving, send info to other players
		If typeofmsg="010"
			For players.player=Each player
				If players\port=UDPMsgPort(stream)
					For player.player=Each player
						If player\username<>players\username
							WriteString(stream,"014"+player\username)
							SendUDPMsg(stream,player\ip,player\port)
						EndIf
					Next
				FreeEntity players\cube
				Delete players
				EndIf
			Next
		EndIf
		
		;Server gets a message and sends it to each player
		If typeofmsg="011"
			For player.player=Each player
				;If the player is not myself or the person who sent it
				If player\ip<>UDPMsgIP(stream) And player\username<>myusername
					;Send out the message
					WriteString(stream,"015"+LSet(player\ip,11)+""+Mid$(readstringstream,4,Len(readstringstream)-4))
					SendUDPMsg stream,player\ip,player\port					
				EndIf
				
				;Add the message to the servers chat box
				If player\ip=UDPMsgIP(stream) And player\username<>myusername
					chatbox.chatbox=New chatbox
					chatbox\message=Mid$(readstringstream,4,Len(readstringstream)-3)
					chatbox\ip=UDPMsgIP(stream)
				EndIf
			Next
		EndIf
		
		;Update movement for each player, this is info from the server
		If typeofmsg="012"
				For players.player=Each player
					If players\port=UDPMsgPort(stream) ;players\ip=UDPMsgIP(stream) And
						If Mid(readstringstream,4,1)=1
							MoveEntity players\cube,0,.3,0
						EndIf
						If Mid(readstringstream,5,1)=1
							TurnEntity players\cube,0,0,4
						EndIf
						If Mid(readstringstream,6,1)=1
							MoveEntity players\cube,0,-.3,0
						EndIf
						If Mid(readstringstream,7,1)=1
							TurnEntity players\cube,0,0,-4
						EndIf
						RotateEntity players\cube,0,0,Int(EntityRoll(players\cube))
					EndIf
				Next

			EndIf
			
			;Send a message to the host to tell them that you are still in the lobby (working)
			If typeofmsg="013"
				WriteString(stream,"009")
				hosts.host=First host
				SendUDPMsg(stream,hosts\ip,hosts\port)
				hosts\lastrecieve=MilliSecs()
			EndIf
			
			;Delete another player that has left
			If typeofmsg="014"
				For player.player=Each player
					If player\username=Mid(readstringstream,4,Len(readstringstream)-3)
						DebugLog player\username
						Stop
						FreeEntity player\cube
						Delete player
					EndIf
				Next
			EndIf
			
			;Add a new message to the chat box (working)
			If typeofmsg="015"
				For player.player=Each player
					If player\ip=Trim$(Mid$(readstringstream,4,11)) And player\username<>myusername
						chatbox.chatbox=New chatbox
						chatbox\message=Mid$(readstringstream,15,Len(readstringstream)-14)
						chatbox\ip=Trim$(Mid$(readstringstream,4,11))
					EndIf
				Next
			EndIf
			
			;Setup game for client
			If typeofmsg="016"
				findmapdistance=True
				For player.player=Each player
					;Start hiding map from player at center
					If player\username=myusername
						hideoriginx=EntityX(player\cube)
						hideoriginy=EntityY(player\cube)
					EndIf
				Next
				programlocation="maingamesetup"
			EndIf
	Wend
End Function