# -*- encoding: utf-8 -*-
from django.contrib import admin
from django.contrib.auth.models import User

from Server.models import RSSFeed,Collection,Account,PostLetters,RSSItem
class AccountAdmin(admin.ModelAdmin):
    list_display = ('username', 'last_name', 'first_name','is_staff', 'is_active')

admin.site.unregister(User)
admin.site.register(Account, AccountAdmin)
admin.site.register(RSSFeed)
admin.site.register(Collection)
admin.site.register(PostLetters)
admin.site.register(RSSItem)
