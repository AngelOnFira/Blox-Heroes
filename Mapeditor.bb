Graphics3D 800,600,16,2
SetBuffer BackBuffer()

Global fileout,ao#,save,bsave,dsave,as,blocko,tex,block,cam,grid,boxsize, xCenter, yCenter

cam=CreateCamera()
light=CreateLight()
blocko=CreateCube()
tex=LoadTexture("Media/mark.png")


Type save
	Field bsave
	Field csave
End Type

Type selectBox
	Field selected, title$
	Field r,g,b
	Field x,y,xLen
End Type

Type textBox
	Field inputString$, title$, selected
	Field x,y
End Type

boxsize=30
xCenter=400
yCenter=300

mapStart()
Dim blocksArray(100,100)
DebugLog e+","+w
mapMain()
FlushKeys()
mapSave()
End

Function mapStart()
	newTextInput("x", 20, 50)
	newTextInput("y", 20, 100)
	newBoxes("Show Border",20,200)
	newBoxes("Add Spawn",20,230)
	newBoxes("Save",20,260)
	newBoxes("Show Border",20,290)
	
End Function

Function mapMain()
	Local gridX, gridY
	While Not KeyHit(1)
		Cls
		
		Color 255,255,0
		
		;Text 0,100, (MouseX()-(xCenter-(Abs(e)/2*boxsize)))/boxsize
		;Text 0,112, (MouseY()-(yCenter-(Abs(w)/2*boxsize)))/boxsize
		;Text 0,130, MouseX()+", "+xCenter+", "+x+", "+boxsize
		
		For thisBox.textBox = Each textBox
			If thisBox\title="x"
				lessThan(thisBox, 100)
				gridX=thisBox\inputString
			Else If thisBox\title="y"
				lessThan(thisBox, 100)
				gridY=thisBox\inputString
			EndIf
			
			drawGrid(gridX, gridY, xCenter, yCenter)
		Next
		
		checkMapMainInputs(gridX, gridY)
		drawMapMain()
		drawTextInput()
		
		If boxSelected("Save")
			deleteSelectBox()
			newTextInput("What do you want to call this file?", 100, 100)
			mapSave()
		EndIf
		
		Color 255,255,255
		Flip
	Wend
End Function

Function mapSave()
	Repeat
		Cls
		
		drawTextInput()
		Flip
		
		If checkInputsSaveMenu()
			this.textBox = First textBox
			testLvlFile=OpenFile("Maps/Blox Heros/"+this\inputString+".BHlvl")
			testEditorFile=OpenFile("Maps/Map Editor/"+this\inputString+".BHME")
			If testLvlFile<>0 Or testEditorFile<>0
				Print "There is already a file with that name. (Press 1 to overwrite, and 2 to re-name)"
				FlushKeys
				ctr=0
				While Not ctr=49 Or ctr=50
					ctr=GetKey()
				Wend
			EndIf
			
			fileout=WriteFile("Maps/Map Editor/"+this\inputString+".BHlvl")
			
			For i = 1 To Abs(gridX)
				currentLine$=""
				For j = 1 To Abs(w)
					currentLine=currentLine+""+blocksArray(i, j)
				Next
				DebugLog currentLine
				WriteLine(fileout, currentLine)
			Next
			CloseFile(fileout)
			Exit
		EndIf
	Forever
End Function

Function save()
End Function

Function Border(abswp,absep)
End Function

Function drawGrid(x#, y#, centerX, centerY)
	x=Abs(x)
	y=Abs(y)
	Color 255,255,255

	For i=1 To x+1
		Line centerX+(x/2*boxsize)-((i-1)*boxsize), centerY-(y/2*boxsize), centerX+(x/2*boxsize)-((i-1)*boxsize), centerY+(y/2*boxsize)
	Next
	
	For i=1 To y+1
		Line centerX-(x/2*boxsize), centerY+(y/2*boxsize)-((i-1)*boxsize), centerX+(x/2*boxsize), centerY+(y/2*boxsize)-((i-1)*boxsize)
	Next
	
		
	Color 255,0,0
	Line GraphicsWidth()/2, 0, GraphicsWidth()/2, GraphicsHeight()
	Line 0, GraphicsHeight()/2, GraphicsWidth(), GraphicsHeight()/2
	Color 255,255,255
	
	
	For i=0 To x
		For j = 0 To y
			If blocksArray(i,j)=1
				Color 0,255,0
			ElseIf blocksArray(i,j)=2
				Color 255,0,0
			EndIf
			
			If blocksArray(i,j)<>0
				Rect centerX-(x/2*boxsize)+(i*boxsize), centerY-(y/2*boxsize)+(j*boxsize), boxsize, boxsize, 1
			EndIf
			Color 255,255,255
		Next
	Next
	
End Function

Function checkMapMainInputs(gridX, gridY)
	If MouseHit(1)
		
		;Check and see if each box was selection box has been chosen
		For boxes.selectBox = Each selectBox
			If MouseX()>=boxes\x And MouseX()<(boxes\x+boxes\xLen) And MouseY()>=boxes\y And MouseY()<(boxes\y+20)
				If boxes\selected=0
					
					For resetBoxes.selectBox = Each selectBox
						resetBoxes\selected=0
						resetBoxes\r=255
						resetBoxes\g=255
						resetBoxes\b=255
					Next
					
					boxes\selected=1
					boxes\r=255
					boxes\g=255
					boxes\b=0
				Else
					boxes\selected=0
					boxes\r=255
					boxes\g=255
					boxes\b=255
				EndIf
			EndIf
		Next
		
		;Check textBoxes
		
		For textBoxes.textBox = Each textBox
			If MouseX()>=textBoxes\x And MouseX()<(textBoxes\x+10+(Len(textBoxes\inputString)*8)) And MouseY()>=(textBoxes\y+19) And MouseY()<(textBoxes\y+39)
				If textBoxes\selected=0
					
					For resettextBoxes.textBox = Each textBox
						resettextBoxes\selected=0
					Next
					textBoxes\selected=1
				Else
					textBoxes\selected=0
				EndIf
			EndIf
		Next
		
		;Check to see what box on grid has been selected
		xMouse=(MouseX()-(xCenter-(Abs(gridX)/2*boxsize)))/boxsize
		yMouse=(MouseY()-(yCenter-(Abs(gridY)/2*boxsize)))/boxsize
		
		If xMouse>=0 And xMouse<Abs(gridX) And yMouse>=0 And yMouse<Abs(gridY)
			If blocksArray(xMouse, yMouse)<>0 And boxSelected("Add Spawn")=False Or blocksArray(xMouse, yMouse)=2
				blocksArray(xMouse, yMouse)=0
			Else
				If boxSelected("Add Spawn")
					blocksArray(xMouse, yMouse)=2
				Else
					blocksArray(xMouse, yMouse)=1
				EndIf
			EndIf
		EndIf
	EndIf
	
	;Check and see if anything was typed
	Repeat
		key=GetKey()
		If key
			For thisBox.textBox = Each textBox
				If thisBox\selected=1
					If key = 8
						thisBox\inputString=Mid(thisBox\inputString, 1, Len(thisBox\inputString)-1)
					Else If key = 13
						Return True
					Else If key
						thisBox\inputString=thisBox\inputString+""+Chr(key)
					EndIf
				EndIf
			Next
			
			
		EndIf
	Until key=0
End Function

Function drawMapMain()
	For boxes.selectBox = Each selectBox
		drawBox(boxes)
	Next
End Function

Function drawBox(boxes.selectBox)
	Color boxes\r,boxes\g,boxes\b
	
	Rect boxes\x,boxes\y,boxes\xLen,20,0
	Text boxes\x+5,boxes\y+4,boxes\title
	
	Color 255,255,255
End Function

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

Function newTextInput(title$, x, y)
	boxes.textBox = New textBox
	
	boxes\title=title
	boxes\inputString=""
	boxes\x=x
	boxes\y=y
End Function

Function deleteSelectBox()
	For boxes.selectBox = Each selectBox
		Delete boxes
	Next
End Function

Function drawTextInput()
	For box.textBox = Each textBox
		Rect box\x,box\y+19,10+(Len(box\inputString)*8),20,0
		If box\selected
			Color 255,0,0
		EndIf
		
		Text box\x+4,box\y+22,box\inputString
		Text box\x-10,box\y+4,box\title
		
		Color 255,255,255
	Next
End Function

Function checkInputsSaveMenu()
	key=GetKey()
	If key = 8
		this.textBox = First textBox
		this\inputString=Mid(this\inputString, 1, Len(this\inputString)-1)
	Else If key = 13
		Return True
	Else If key
		this.textBox = First textBox
		this\inputString=this\inputString+""+Chr(key)
	EndIf
End Function

Function lessThan(textBoxes.textBox, num)
	If Len(textBoxes\inputString)>0
		While Len(textBoxes\inputString)>0
			If (Asc(Mid$(textBoxes\inputString, Len(textBoxes\inputString), 1))<48 Or Asc(Mid$(textBoxes\inputString, Len(textBoxes\inputString), 1))>57)
				If Len(textBoxes\inputString)=1
					textBoxes\inputString=""
				Else
					textBoxes\inputString=Mid(textBoxes\inputString,1,Len(textBoxes\inputString)-1)
				EndIf
			Else
				Exit
			EndIf
		Wend
		
		While Int(textBoxes\inputString)>100
			textBoxes\inputString=Mid(textBoxes\inputString,1,Len(textBoxes\inputString)-1)
		Wend
	EndIf
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D