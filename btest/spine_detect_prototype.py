import numpy as np
import cv2

from matplotlib import pyplot as plt

paths = ["../data/IMG_20210213_151220.jpg","../data/IMG_20210213_151236.jpg","../data/IMG_20210213_151546.jpg","../data/IMG_20210213_151504.jpg","../data/IMG_20210213_152029.jpg"]
def spine_detect(IMG_PATH):
	img = cv2.imread(IMG_PATH)

	# DOWN_SCALE
	scale_percent = 30
	new_dim = (	int(img.shape[1] * scale_percent / 100),
	            int(img.shape[0] * scale_percent / 100))
	img = cv2.resize(img, new_dim, interpolation = cv2.INTER_AREA)

	# B&W
	img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
	# histogram equilization
	img = cv2.equalizeHist(img)

	# edge detection
	edges = cv2.Canny(img, 100, 150)

	# histogram
	histSize = 256
	hist = cv2.calcHist([img], [0], None, [histSize], [0,255])

	for i in range(1, histSize):
	    cv2.line(	img,
	                ( (i-1), int(0.01*round(hist[i-1][0]))),
	                ( (  i), int(0.01*round(hist[  i][0]))),
	                ( 255, 0, 0),
	                thickness=2)

	# edges = cv2.adaptiveThreshold(edges, 255, 1, 1, 11, 2)
	img = cv2.add(img, edges)

	edges = cv2.dilate(edges,None,iterations = 2)
	edges = cv2.erode(edges,None,iterations = 2)

	contours, hierarchy = cv2.findContours( edges,
	                                        cv2.RETR_TREE,
	                                        cv2.CHAIN_APPROX_SIMPLE)

	#Refined

	index = 0 
	for cnt in contours:
		x,y,w,h = cv2.boundingRect(cnt)
		if(w/h < 50):
			contours.pop(index)
			continue
		index=index+1

	#Find biggest area
	area = 0
	for cnt in contours:
		x,y,w,h = cv2.boundingRect(cnt)
		if(w*h > area):
			area = w*h

	index = 0
	for cnt in contours:
		x,y,w,h = cv2.boundingRect(cnt)
		if(w*h*5 < area):
			contours.pop(index)
			continue
		index = index + 1

	#Drawing loop
	for cnt in contours:
		x,y,w,h = cv2.boundingRect(cnt)
		if(w*h*5 > area):
			cv2.rectangle(img,(x,y),(x+w,y+h),(0,0,0),2)

	cv2.imshow("Refined Contours", img)
	cv2.waitKey(0)
	cv2.destroyAllWindows()

for path in paths:
	spine_detect(path)