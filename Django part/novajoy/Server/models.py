# -*- encoding: utf-8 -*-
from django.db import models
from django.db import models
from django.contrib.auth.models import User, UserManager
from django.contrib.auth.models import BaseUserManager
from django.utils import timezone

class MyUserManager(BaseUserManager):

    def create_user(self, username, email=None, password=None,age=None,update=None, **extra_fields):
        """
        Creates and saves a User with the given username, email and password.
        """
        now = timezone.now()
        if not username:
            raise ValueError('The given username must be set')
        email = UserManager.normalize_email(email)
        user = self.model(username=username, email=email,age=age,update=update,
                          is_staff=False, is_active=True, is_superuser=False,
                          last_login=now, date_joined=now, **extra_fields)

        user.set_password(password)
        user.save(using=self._db)
        return user

    def create_superuser(self, username, email, password,age,update, **extra_fields):
        u = self.create_user(username, email, password,age,update **extra_fields)
        u.is_staff = True
        u.is_active = True
        u.is_superuser = True
        u.save(using=self._db)
        return u

class Account(User):
    is_staff = False
    age = models.PositiveSmallIntegerField(null=False, blank=True)
    update = models.TimeField(null=False, blank=True)
    objects = MyUserManager()

    def __unicode__(self):
        return self.username

class PostLetters(models.Model):
    target = models.CharField(max_length=50,null=False, blank=True)
    title = models.CharField(max_length=100,null=False, blank=True)
    body = models.TextField(null=False, blank=True)
    attachment = models.TextField(null=True)

    def __unicode__(self):
        return self.target

class Collection(models.Model):
    user = models.ForeignKey(Account,null=False, blank=True)
    name_collection = models.CharField(max_length=100,null=False, blank=True)
    update_time = models.TimeField(null=False, blank=True)

    def __unicode__(self):
        return self.name_collection

class RSSFeed(models.Model):
    #name = models.ManyToManyField(User)
    collection = models.ManyToManyField(Collection,null=False, blank=True)
    url = models.URLField(null=False, blank=True)
    #content = models.TextField(blank=True,null=True)
    pubDate = models.DateTimeField(null=False, blank=True)

    def __unicode__(self):
        return self.url

class RSSItem(models.Model):
    rssfeed = models.ForeignKey(RSSFeed,null=True, blank=True)
    title = models.CharField(max_length=222,null=True, blank=True)
    description = models.TextField(null=False, blank=True)
    link = models.URLField(null=False, blank=True)
    author = models.CharField(max_length=30,null=True, blank=True)
    pubDate = models.DateTimeField(null=False, blank=True)

    def __unicode__(self):
        return self.title

class Category(models.Model):
    rssitem = models.ForeignKey(RSSFeed,null=False, blank=True)
    category = models.CharField(max_length=50,null=False, blank=True)

    def __unicode__(self):
        return self.category


