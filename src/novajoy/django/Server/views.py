# -*- encoding: utf-8 -*-
from django.http import HttpResponse,HttpResponseRedirect,Http404
from django.shortcuts import render_to_response,RequestContext
from Server.models import Collection,Account,RSSFeed
from django.core import serializers
from datetime import datetime

def viewCollection(request):
    if not request.user.is_authenticated():
        return HttpResponseRedirect('/accounts/login/?next=%s' % request.path)
    try:
        user = Account.objects.get(username=request.user.username)
        collection = Collection.objects.filter(user=user)
        return render_to_response('mainPage.html',{'collection':collection},context_instance=RequestContext(request))
    except Account.DoesNotExist:
        return HttpResponse("This User don't have collections")

def viewURL(request):
    if request.POST.get('nameCollection') is None:
        return HttpResponse("Empty nameCollection")
    user = Account.objects.get(username=request.user.username)
    collection = Collection.objects.get(user=user,name_collection=request.POST['nameCollection'])
    rss = RSSFeed.objects.filter(collection=collection)
    mimetype = 'application/javascript'
    data = serializers.serialize('json', rss)
    return HttpResponse(data,mimetype)

def addCollection(request):
    #to write a decorator for definition ativete user
    if request.POST.get('newCollection') is None:
        return HttpResponse("Empty field newCollection")
    response = HttpResponse()
    response['Content-Type'] = "text/javascript"
    user = Account.objects.get(username=request.user.username)
    c = Collection(user=user,name_collection=request.POST['newCollection'],delta_update_time=30,last_update_time=datetime.now())
    c.save()

    response.write("Success")
    return HttpResponse(response)

def addRSS(request):
    if request.POST.get('nameOfNewRSS') is None:
        return HttpResponse("Empty field nameOfNewRSS")
    response = HttpResponse()
    response['Content-Type'] = "text/javascript"
    user = Account.objects.get(username=request.user.username)
    collection = Collection.objects.get(user=user,name_collection=request.POST['nameCollection'])
    newRSS = RSSFeed(url=request.POST.get('nameOfNewRSS'),pubDate='2013-03-31 13:10:32')
    newRSS.save()
    newRSS.collection.add(collection)
    newRSS.save()
    return HttpResponse("Success")


