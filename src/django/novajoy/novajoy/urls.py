from django.conf.urls import patterns, include, url
from Server.views import *
from django.conf import settings
from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
                       url(r'^captcha/', include('captcha.urls')),
                       url(r'^$',viewCollection),
                       url(r'^addCollection/$',addCollection),
                       url(r'^accounts/', include('backends.urls')),
                       url(r'^admin/', include(admin.site.urls)),
                       )
