from django.conf.urls import patterns, include, url
from Server.views import *
from django.conf import settings
from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
                       url(r'^captcha/', include('captcha.urls')),
                       url(r'^$',viewCollection),
                       url(r'^selectURL/$',viewURL),
                       url(r'^addCollection/$',addCollection),
                       url(r'^addRSS/$',addRSS),
                       url(r'^accounts/passwordReset/$',resetPassword),
                       url(r'^accounts/passwordReset/confirm/(?P<activation_key>\w+)$',resetPasswordConfirm),
                       url(r'^accounts/', include('backends.urls')),
                       url(r'^admin/', include(admin.site.urls)),
                       )
