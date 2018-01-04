import os
import sys
import time

#Try to import external packages
exitflag = False
exitmsg = 'Failed to import, perhaps some packages are not installed?'
try:
  import serial
  from serial.tools import list_ports
except:
  exitmsg += '\nRun: py -3 -m pip install pySerial'
  exitflag = True
try:
  from networktables import NetworkTables
except:
  exitmsg += '\nRun: py -3 -m pip install pynetworktables'
  exitflag = True

#-----SETUP-----#
ip = 'roborio-4537-frc.local' #IP or hostname of networktable host
tablePath = 'PythonListener' #Path of networktable
sdb = "C:\\Users\\bryce\\wpilib\\tools\\SmartDashboard.jar" #Dashboard location
sdbs = False #Start dashboard?
baud = 9600 #Arduino baud rate
keys = ['str', 'str2'] #Key strings within table
verbose = True #Extra debug info
#---------------#

def debug(message):
  #Print debug info
  if(verbose == True):
    print(message)

def connect():
  arduino = ''
  #Loop until connected
  while(arduino == ''):
    debug('\nAttempting to connect to first avaliable Arduino')
    #List all comports
    comports = list_ports.comports()
    if(comports != []):
      #Connect to each port
      for port in comports:
        debug('\nAttempting to connect to Arduino on port {}'.format(port[0]))
        try:
          arduino = serial.Serial(port[0], baud)
          debug('Connected to Arduino on port {}'.format(port[0]))
          return arduino
        except:
          debug('Failed to connect to Arduino on port {}'.format(port[0]))
      if(arduino == ''):
        debug('Could not connect to any Arduinos\nRetrying in 5 seconds...')
        time.sleep(5)
    else:
      debug('No ports detected\nRetrying in 5 seconds...')
      time.sleep(5)

#Start Smart Dashboard
if(sdbs):
  try:
    os.startfile(sdb)
    debug('Started Smart Dashboard')
  except:
    debug('Failed to start Smart Dashboard, perhaps file path is wrong?')

#Exit if packages are not installed
if(exitflag):
  print(exitmsg)
  input('Press <ENTER> to exit...')
  exit()

#Connect to Arduino
arduino = connect()

#Connect to RoboRIO
debug('\nConnecting to RoboRIO on {}'.format(ip))
debug('Table: /Root/{}'.format(tablePath))
NetworkTables.initialize(server=ip)
table = NetworkTables.getTable(tablePath)
time.sleep(2.5)

#Create table to store previous table for change checking
lastValues = []
for i in keys:
  lastValues.append('')

debug('\n-----MAIN MONITOR RUNNING-----')
while True:
  #Loop through keys
  for i in range(len(keys)):
    try:  
      value = table.getString(keys[i])
    except KeyError:
      value = ''
    
    #Check if key has changed
    if((lastValues[i] != value) & (value != '')):
      debug('\nKey: "{}" changed\nsending "{}" to Arduino'.format(keys[i], value))
      try:
        #Send to Arduino
        arduino.write(value.encode('utf-8'))
        lastValues[i] = value
      except serial.serialutil.SerialException:
        #Send failed, reconnect to Arduino
        debug('Failed to transmit, lost connection?\nAttempting reconnect in 5 seconds...')
        time.sleep(5)
        arduino = connect()
  time.sleep(0.1)