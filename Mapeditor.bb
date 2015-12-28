Graphics3D 800,600,16,2
SetBuffer BackBuffer()

Global boxsize, xCenter, yCenter

;Create the type to hold the selection boxes
Type selectBox
	Field selected, title$
	Field r,g,b
	Field x,y,xLen
End Type

;Type to hold the boxes with user input
Type textBox
	Field inputString$, title$, selected
	Field x,y
End Type

;Set program constants so that they can easily be changed
boxsize=30
xCenter=400
yCenter=300

;Main program direction
mapStart()
Dim blocksArray(100,100)
mapMain()
FlushKeys()
End

Function mapStart()
	;Set up the boxes we need for the GUI
	newTextInput("x", 20, 50)
	newTextInput("y", 20, 100)
	newBoxes("Show Border",20,200)
	newBoxes("Add Spawn",20,230)
	newBoxes("Save",20,260)
	newBoxes("Show Border",20,290)
End Function

Function mapMain()
	;Set the local variables, we will pass them into functions when needed
	Local gridX, gridY
	
	;Main loop for the main function
	While Not KeyHit(1)
		Cls
		
		;Make sure all of the user input boxes don't have inproper values
		For thisBox.textBox = Each textBox
			If thisBox\title="x"
				lessThan(thisBox, 100)
				gridX=thisBox\inputString
			Else If thisBox\title="y"
				lessThan(thisBox, 100)
				gridY=thisBox\inputString
			EndIf
			
			;Then draw the grid with the user values
			drawGrid(gridX, gridY, xCenter, yCenter)
		Next
		
		;Check for any inputs, with the boundries being what the user set
		checkMapMainInputs(gridX, gridY)
		
		;Draw the map
		drawMapMain()
		
		;Draw if the user has anything in the input boxes
		drawTextInput()
		
		;If the user clicked save, then save
		If boxSelected("Save")
			;Delete any select boxes that are being used
			deleteSelectBox()
			;Make a new user input to get the file name
			newTextInput("What do you want to call this file?", 100, 100)
			;Save the map
			mapSave(gridX, gridY)
		EndIf
		
		Color 255,255,255
		Flip
	Wend
End Function

Function mapSave(gridX, gridY)
	;Save the map
	While value$=""
		Cls
		
		;Draw the input boxes for the user
		drawTextInput()
		Flip
		
		;Check what the user is inputing
		value$ = checkInputsSaveMenu()
		
		;If the user hit enter when we check what they do
		If value<>""
			
			;Check if the file is already being used (not working)
			testLvlFile=OpenFile("Maps/Blox Heros/"+value+".BHlvl")
			testEditorFile=OpenFile("Maps/Map Editor/"+value+".BHME")
			
			If testLvlFile<>0 Or testEditorFile<>0
				Print "There is already a file with that name. (Press 1 to overwrite, and 2 to re-name)"
				FlushKeys
				ctr=0
				While Not ctr=49 Or ctr=50
					ctr=GetKey()
				Wend
			EndIf
			
			;Make a new file for the user's map
			fileout=WriteFile("Maps/Blox Heros/"+value+".BHlvl")
			
			;Insert the x and y dims of the map at the top
			WriteLine(fileout, LSet(gridX,3)+""+LSet(gridY,3))
			
			;Save the rest of the map
			For i = 1 To gridX
				currentLine$=""
				For j = 1 To gridY
					currentLine=currentLine+""+blocksArray(i, j)
				Next
				WriteLine(fileout, currentLine)
			Next
			
			;Finish and close the file
			CloseFile(fileout)
		EndIf
	Wend
End Function

;Need to add border
Function Border(abswp,absep)
End Function

;Function to draw grid with user sizes
Function drawGrid(x#, y#, centerX, centerY)
	Color 255,255,255
	
	;Draw the horizontal lines of the grid
	For i=1 To x+1
		Line centerX+(x/2*boxsize)-((i-1)*boxsize), centerY-(y/2*boxsize), centerX+(x/2*boxsize)-((i-1)*boxsize), centerY+(y/2*boxsize)
	Next
	
	;Draw the vertical lines of the grid
	For i=1 To y+1
		Line centerX-(x/2*boxsize), centerY+(y/2*boxsize)-((i-1)*boxsize), centerX+(x/2*boxsize), centerY+(y/2*boxsize)-((i-1)*boxsize)
	Next
	
	
	;Temp center lines
	Color 255,0,0
	Line GraphicsWidth()/2, 0, GraphicsWidth()/2, GraphicsHeight()
	Line 0, GraphicsHeight()/2, GraphicsWidth(), GraphicsHeight()/2
	Color 255,255,255
	
	;Fill in the spots of the grid if they are selected
	For i=0 To x
		For j = 0 To y
			;Choose what colour to fill them with
			If blocksArray(i,j)=1
				Color 0,255,0
			ElseIf blocksArray(i,j)=2
				Color 255,0,0
			EndIf
			
			;Fill the spot
			If blocksArray(i,j)<>0
				Rect centerX-(x/2*boxsize)+(i*boxsize), centerY-(y/2*boxsize)+(j*boxsize), boxsize, boxsize, 1
			EndIf
			Color 255,255,255
		Next
	Next
	
End Function

;Funciton to handle all input in the main menu
Function checkMapMainInputs(gridX, gridY)
	;Only check when the mouse has been hit
	If MouseHit(1)
		;Check with every box and see if it was selected
		For boxes.selectBox = Each selectBox
			;If it was clicked
			If MouseX()>=boxes\x And MouseX()<(boxes\x+boxes\xLen) And MouseY()>=boxes\y And MouseY()<(boxes\y+20)
				;Change its value
				If boxes\selected=0
					;Set all of the other boxes to 0, and reset their colour
					For resetBoxes.selectBox = Each selectBox
						resetBoxes\selected=0
						resetBoxes\r=255
						resetBoxes\g=255
						resetBoxes\b=255
					Next
					
					;Select this box, change its colour
					boxes\selected=1
					boxes\r=255
					boxes\g=255
					boxes\b=0
				Else
					;Otherwise, set the box to 0 (is this really needed?)
					boxes\selected=0
					boxes\r=255
					boxes\g=255
					boxes\b=255
				EndIf
			EndIf
		Next
		
		;Check textBoxes
		For textBoxes.textBox = Each textBox
			;If it was selected
			If MouseX()>=textBoxes\x And MouseX()<(textBoxes\x+10+(Len(textBoxes\inputString)*8)) And MouseY()>=(textBoxes\y+19) And MouseY()<(textBoxes\y+39)
				;If it is not selected
				If textBoxes\selected=0
					;Unselect all of the others
					For resettextBoxes.textBox = Each textBox
						resettextBoxes\selected=0
					Next
					;Set this one to selected
					textBoxes\selected=1
				Else
					;Otherwise deselect everything else (is it really needed?)
					textBoxes\selected=0
				EndIf
			EndIf
		Next
		
		;Check to see what box on grid has been selected
		xMouse=(MouseX()-(xCenter-(Abs(gridX)/2*boxsize)))/boxsize
		yMouse=(MouseY()-(yCenter-(Abs(gridY)/2*boxsize)))/boxsize
		
		;If it's within the grid
		If xMouse>=0 And xMouse<gridX And yMouse>=0 And yMouse<gridY
			;If we need to reset the spot
			If blocksArray(xMouse, yMouse)<>0 And boxSelected("Add Spawn")=False Or blocksArray(xMouse, yMouse)=2
				blocksArray(xMouse, yMouse)=0
			Else
				;If we are adding a spawn
				If boxSelected("Add Spawn")
					blocksArray(xMouse, yMouse)=2
				;Otherwise if we are adding a normal wall
				Else
					blocksArray(xMouse, yMouse)=1
				EndIf
			EndIf
		EndIf
	EndIf
	
	;Check and see if anything was typed. repeat until there are no more keys to get
	Repeat
		;Get the last typed key
		key=GetKey()
		
		;If there was a key
		If key
			;For each textBox there is
			For thisBox.textBox = Each textBox
				;If its selected
				If thisBox\selected=1
					;If the key is backspace, remove a character from the end
					If key = 8
						thisBox\inputString=Mid(thisBox\inputString, 1, Len(thisBox\inputString)-1)
					;If the key is enter, return true
					Else If key = 13
						Return True
					;Otherwise add the character to the string
					Else If key
						thisBox\inputString=thisBox\inputString+""+Chr(key)
					EndIf
				EndIf
			Next
		EndIf
	Until key=0
End Function

;Function to draw the main program
Function drawMapMain()
	For boxes.selectBox = Each selectBox
		drawBox(boxes)
	Next
End Function

;Function that draws the select boxes of the main menu
Function drawBox(boxes.selectBox)
	;Set the colour
	Color boxes\r,boxes\g,boxes\b
	
	;Display it
	Rect boxes\x,boxes\y,boxes\xLen,20,0
	Text boxes\x+5,boxes\y+4,boxes\title
	
	;Reset the colour
	Color 255,255,255
End Function

;Constructor for a new select box
Function newBoxes(title$, x, y)
	boxes.selectBox = New selectBox
	
	boxes\title=title
	boxes\x=x
	boxes\y=y
	boxes\xLen=10+(Len(title)*8)
	boxes\r=255
	boxes\g=255
	boxes\b=255
End Function

;Find if the box was selected, return result
Function boxSelected(title$)
	For boxes.selectBox = Each selectBox
		If boxes\title=title
			If boxes\selected=1
				Return True
			Else
				Return False
			EndIf
		EndIf
	Next
End Function

;Contructor for a new textBox
Function newTextInput(title$, x, y)
	boxes.textBox = New textBox
	
	boxes\title=title
	boxes\inputString=""
	boxes\x=x
	boxes\y=y
End Function

;Function to delete unneeded select boxes
Function deleteSelectBox()
	For boxes.selectBox = Each selectBox
		Delete boxes
	Next
End Function

;Draw all of the text input to the screen
Function drawTextInput()
	;Go through each text box
	For box.textBox = Each textBox
		;Draw the rectangle
		Rect box\x,box\y+19,10+(Len(box\inputString)*8),20,0
		
		;Change the colour if it's selected
		If box\selected
			Color 255,0,0
		EndIf
		
		;Change add the text of the input, as well as the question text
		Text box\x+4,box\y+22,box\inputString
		Text box\x-10,box\y+4,box\title
		
		Color 255,255,255
	Next
End Function

;Function to check the inputs of the save menu
Function checkInputsSaveMenu$()
	;When the mouse is hit, see if anything was selected
	If MouseHit(1)
		;Go through each box
		For textBoxes.textBox = Each textBox
			;If the click was within a box
			If MouseX()>=textBoxes\x And MouseX()<(textBoxes\x+10+(Len(textBoxes\inputString)*8)) And MouseY()>=(textBoxes\y+19) And MouseY()<(textBoxes\y+39)
				;If it was not selected
				If textBoxes\selected=0
					;Unselect every other box
					For resettextBoxes.textBox = Each textBox
						resettextBoxes\selected=0
					Next
					
					;Select this one
					textBoxes\selected=1
				;Otherwise unselect every other box (is this really needed?)
				Else
					textBoxes\selected=0
				EndIf
			EndIf
		Next
	EndIf
	
	Repeat
		;Get the last key typed
		key=GetKey()
		
		;As long as there is another key to look at
		If key
			;Go through each textBox
			For this.textBox = Each textBox
				;If it was selected
				If this\selected=1
					;If backspace was hit, remove the last character
					If key = 8
						this\inputString=Mid(this\inputString, 1, Len(this\inputString)-1)
					;If the enter key was hit, return the file name
					Else If key = 13 And this\title="What do you want to call this file?"
						Return this\inputString$
					;Otherwise add the key to the selected box
					Else If key
						this\inputString=this\inputString+""+Chr(key)
					EndIf
				EndIf
			Next
		EndIf
	;Repeat until there are no more key presses to look at
	Until key=0
End Function

;Function to check that the text box is less then num and dosen't have any bad characters
Function lessThan(textBoxes.textBox, num)
	;As long as the text is longer than 0
	If Len(textBoxes\inputString)>0
		;While its longer than 0
		While Len(textBoxes\inputString)>0
			;If it's not a number
			If (Asc(Mid$(textBoxes\inputString, Len(textBoxes\inputString), 1))<48 Or Asc(Mid$(textBoxes\inputString, Len(textBoxes\inputString), 1))>57)
				;If the length of the string is only 1, set the string to ""
				If Len(textBoxes\inputString)=1
					textBoxes\inputString=""
				;Otherwise remove a character from the end
				Else
					textBoxes\inputString=Mid(textBoxes\inputString,1,Len(textBoxes\inputString)-1)
				EndIf
			Else
				;If it's not a letter, we can exit early
				Exit
			EndIf
		Wend
		
		;Remove characters from the end till the string is less than the given number
		While Int(textBoxes\inputString)>num
			textBoxes\inputString=Mid(textBoxes\inputString,1,Len(textBoxes\inputString)-1)
		Wend
	EndIf
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D