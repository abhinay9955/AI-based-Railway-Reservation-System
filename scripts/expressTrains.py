import csv
from selenium import webdriver
import time
from selenium.webdriver.common.keys import Keys

def loadurl(url,row):
	if True:
		try:
			driver.get(url)
			driver.find_element_by_id("ui-id-9").click()
			driver.find_element_by_id("avgDelayTrainInput").clear()
			driver.find_element_by_id("avgDelayTrainInput").send_keys(row[1])
			time.sleep(4)
			try:
				tr = driver.find_element_by_id("trainScheduleAvgDelayDispTbl").find_elements_by_tag_name("tr")
				arrDelay = ""
				deptDelay = ""
				for j in range(0,len(tr)):
					td = tr[j].find_elements_by_tag_name("td")
					arrDelay = arrDelay + "$" + td[2].text
					deptDelay = deptDelay + "$" + td[3].text
				with open("routesData1.csv","a") as writeFile:
					file = csv.writer(writeFile)
					file.writerow([row[1]]+[arrDelay]+[deptDelay])
				return

			except Exception as ee:
				print(str(ee)+" this")
				with open("routesData1.csv","a") as writeFile:
					file = csv.writer(writeFile)
					file.writerow([row[1]]+[""]+[""])
				return

		except Exception as e:
			print(str(e)+" maybe")
			loadurl(url,row)
	return

driver = webdriver.Chrome("/usr/lib/chromium-browser/chromedriver")
url = "https://enquiry.indianrail.gov.in/ntes/index.html"

with open("routesData1.csv","w") as writeFile:
	file = csv.writer(writeFile)
	file.writerow(["Train Number"]+["Arrival Delays"]+["Departure Delays"])

i = 0
with open("reservedTrains.csv","r") as readFile:
	file = csv.reader(readFile)
	for row in file:
		loadurl(url,row)