#coding: utf-8
from django.db import models

class User(models.Model):
    user_name = models.CharField(max_length=30,primary_key=True)
    password = models.CharField(max_length=50)
    email  = models.EmailField()
    #update_time = models.TimeField()

    def __unicode__(self):
        return self.user_name

class Collection(models.Model):
    user = models.ForeignKey(User,blank=False)
    name_collection = models.CharField(max_length=30)
    update_time = models.TimeField()

    def __unicode__(self):
        return self.name_collection

class RSSFeed(models.Model):
    #name = models.ManyToManyField(User)
    collection = models.ManyToManyField(Collection)
    url = models.URLField()
    content = models.TextField(blank=True,null=True)
    date_adding = models.DateField()

    def __unicode__(self):
        return self.url
