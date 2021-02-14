import cv2 as cv
import sys
import numpy as np

def show_img(img):
	cv.imshow("Pretty Pictureeee", img)
	k = cv.waitKey(0)
	if k == ord("s"):
		cv.imwrite("easy_out.jpg",img)

def preprocess(img):
	img = cv.imread(cv.samples.findFile("easy.jpg"))
	if img is None:
		sys.exit("Could not read the image :(")

	#Display Original~
	show_img(img)

	#Denoise
	img = cv.fastNlMeansDenoisingColored(img,None,10,10,7,21)
	show_img(img)

	#Greyscale
	if(len(img.shape) != 2):
		Gimg = cv.cvtColor(img, cv.COLOR_BGR2GRAY)
	else:
		Gimg = img

	#AdaptiveThreshold(??)
	Gimg = cv.bitwise_not(Gimg)
	bitty = cv.adaptiveThreshold(Gimg, 225, cv.ADAPTIVE_THRESH_MEAN_C, cv.THRESH_BINARY, 15, -2)

	#Display BW image
	show_img(bitty)

	return bitty


def findlines(img):
	Pimg = preprocess(img)
	
	ret, thresh = cv.threshold(Pimg, 127, 255, 0) #i think this is doing nothing
	im2, contours= cv.findContours(thresh, cv.RETR_TREE, cv.CHAIN_APPROX_SIMPLE)
	#show_img(im2)
	Pimg = cv.drawContours(Pimg, contours, -1, (0,255,0),3)
	show_img(Pimg)



#Main Code
findlines("easy.jpg")
