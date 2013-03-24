from django.conf.urls import patterns, include, url
from Server.views import mainPage
# Uncomment the next two lines to enable the admin:
from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
                       url(r'^accounts/', include('backends.urls')),
                       #url(r'^register/$', register, {'backend': 'app.backend.RegBackend', 'form_class': CustomRegForm}, name='registration_register'),
                       #url(r'^accounts/', include('registration.urls')),
                       url(r'^admin/', include(admin.site.urls)),
                       )
