from django.conf.urls import patterns, include, url
from Server.views import *
from django.conf import settings
# Uncomment the next two lines to enable the admin:
from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
                       url(r'^hello/$',hello),
                       url(r'^save/$',save),
                       url(r'^$',test),
                       url(r'^addCollection/$',addCollection),
                       #url(r'^addCollection/(?P<name>\w+)/$',addCollection),
                       url(r'^accounts/', include('backends.urls')),
                       #url(r'^register/$', register, {'backend': 'app.backend.RegBackend', 'form_class': CustomRegForm}, name='registration_register'),
                       #url(r'^accounts/', include('registration.urls')),
                       url(r'^admin/', include(admin.site.urls)),
                       )
