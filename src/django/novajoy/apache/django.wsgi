import os
import sys
 
path = '/srv/www/novajoy'
if path not in sys.path:
    sys.path.insert(0, path)
 
os.environ['DJANGO_SETTINGS_MODULE'] = 'novajoy.settings'
 
import django.core.handlers.wsgi
application = django.core.handlers.wsgi.WSGIHandler()
