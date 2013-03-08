from django.db import models

class User(models.Model):
    user_name = models.CharField(max_length=30,primary_key=True)
    password = models.CharField(max_length=50)
    email  = models.EmailField()
    update_time = models.TimeField()

    def __unicode__(self):
        return self.user_name

class RSSFeed(models.Model):
    name = models.ManyToManyField(User)
    url = models.URLField()
    content = models.TextField()
    date_adding = models.DateField()

    def __unicode__(self):
        return self.url
