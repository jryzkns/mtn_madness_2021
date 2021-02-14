import numpy as np
import cv2

from matplotlib import pyplot as plt

IMG_PATH = "IMG_20210213_151815.jpg"

img = cv2.imread(IMG_PATH)

# DOWN_SCALE
scale_percent = 30
new_dim = (	int(img.shape[1] * scale_percent / 100),
            int(img.shape[0] * scale_percent / 100))
img = cv2.resize(img, new_dim, interpolation = cv2.INTER_AREA)

# B&W
img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

# edge detection
edges = cv2.Canny(img, 100, 150)
img = cv2.add(img, edges)

# histogram
histSize = 256
hist = cv2.calcHist([img], [0], None, [histSize], [0,255])

for i in range(1, histSize):
    cv2.line(	img,
                ( (i-1), int(0.01*round(hist[i-1][0]))),
                ( (  i), int(0.01*round(hist[  i][0]))),
                ( 255, 0, 0),
                thickness=2)


cv2.imshow(IMG_PATH, img)
cv2.waitKey(0)
cv2.destroyAllWindows()