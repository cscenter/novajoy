import ConfigParser

class iniWorker():
    def __init__(self):
        self.config = ConfigParser.ConfigParser()
        try:
            self.config.read('./../../../config/config.ini')
        except NameError:
            print "This ini-file is not found"
            raise

    def getAddress(self):
        return self.config.get('database', 'address')

    def getBase(self):
        return self.config.get('database', 'base')

    def getUser(self):
        return self.config.get('database', 'user')

    def getPassword(self):
        return self.config.get('database', 'password')