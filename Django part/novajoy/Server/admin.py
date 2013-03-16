#coding: utf-8
from django.contrib import admin
from Server.models import User, RSSFeed,Collection

admin.site.register(User)
admin.site.register(RSSFeed)
admin.site.register(Collection)
