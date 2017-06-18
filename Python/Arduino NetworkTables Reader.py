import os
import sys
import time

try:
  os.startfile("C:\\Users\\bryce\\wpilib\\tools\\SmartDashboard.jar")
  print('Started Smart Dashboard')
except:
  print('Failed to start Smart Dashboard, perhaps file path is wrong?')

exitflag = False
exitmsg = 'Failed to import, perhaps packages are not installed?'
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

if(exitflag):
  print(exitmsg)
  exit()

ip = '127.0.0.1'
tablePath = 'PythonListener'
baud = 9600
keys = ['str', 'str2']
verbose = True

def debug(message):
  if(verbose == True):
    print(message)

def connect():
  arduino = ''
  while(arduino == ''):
    debug('\nAttempting to connect to first avaliable Arduino')
    comports = list_ports.comports()
    if(comports != []):
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

arduino = connect()

debug('\nConnecting to RoboRIO on {}'.format(ip))
NetworkTables.initialize(server=ip)

table = NetworkTables.getTable(tablePath)
time.sleep(2.5)

lastValues = []
for i in keys:
  lastValues.append('')

debug('\n-----MAIN MONITOR RUNNING-----')
while True:
  for i in range(len(keys)):
    try:  
      value = table.getString(keys[i])
    except KeyError:
      value = ''
    
    if((lastValues[i] != value) & (value != '')):
      lastValues[i] = value
      debug('\nKey: "{}" changed\nsending "{}" to Arduino'.format(keys[i], value))
      try:
        arduino.write(value.encode('utf-8'))
      except serial.serialutil.SerialException:
        debug('Failed to transmit, lost connection?\nAttempting reconnect in 5 seconds...')
        time.sleep(5)
        arduino = connect()
  time.sleep(0.1)