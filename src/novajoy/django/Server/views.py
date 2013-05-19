# -*- encoding: utf-8 -*-
from django.http import HttpResponse,HttpResponseRedirect,Http404
from django.shortcuts import render_to_response,RequestContext
from Server.models import Collection,Account,RSSFeed
from django.core import serializers
from datetime import datetime
from Server.models import PostLetters
from django.utils.hashcompat import sha_constructor
from django.contrib.auth.decorators import user_passes_test
from django.template.loader import render_to_string
from Server.forms import ResetPassword,Password
import random
import feedparser
import string
from django import template
from datetime import time
import datetime
from django.template import loader, Context


def isAuth(user):
    if user.username=="novajoyUser":
        return False
    return user.is_authenticated()

def isRss(RSSUrl):
    feed = feedparser.parse(RSSUrl)
    if feed['bozo']==1 :
        return False
    else:
        return True

@user_passes_test(isAuth,login_url="/accounts/login/")
def viewCollection(request):
    if not request.user.is_authenticated():
        return HttpResponseRedirect('/accounts/login/?next=%s' % request.path)
    try:
        user = Account.objects.get(username=request.user.username)
        collection = Collection.objects.filter(user=user)
        return render_to_response('mainPage.html',{'collection':collection,'user_name':user.username},context_instance=RequestContext(request))
    except Account.DoesNotExist:
        return HttpResponse("This User don't have collections")

@user_passes_test(isAuth,login_url="/accounts/login/")
def viewURL(request):
    if request.POST.get('nameCollection') is None:
        return HttpResponse("Empty nameCollection")
    user = Account.objects.get(username=request.user.username)
    collection = Collection.objects.get(user=user,name_collection=request.POST['nameCollection'])
    rss = RSSFeed.objects.filter(collection=collection)
    mimetype = 'application/javascript'
    data = serializers.serialize('json', rss)
    return HttpResponse(data,mimetype)

@user_passes_test(isAuth,login_url="/accounts/login/")
def addCollection(request):
    #to write a decorator for definition ativete user
    if request.POST.get('newCollection') is None:
        return HttpResponse("Empty field newCollection")
    response = HttpResponse()
    response['Content-Type'] = "text/javascript"
    user = Account.objects.get(username=request.user.username)
    if Collection.objects.filter(user=user,name_collection=request.POST['newCollection']).__len__()>0:
        response.write("The collection with such name already exists")
        return HttpResponse(response)
    delta_sending_time = request.POST['delta_sending_time'];
    interval_sec = 0
    if "min" in delta_sending_time:
        interval_sec = int(delta_sending_time[:string.find(delta_sending_time,"min")])*60
    elif "h" in delta_sending_time:
        interval_sec = int(delta_sending_time[:string.find(delta_sending_time,"h")]) * 60 * 60
    else:
        interval_sec = int(delta_sending_time[:string.find(delta_sending_time,"h")]) * 60 * 60 * 24

        # c = Collection(user=user,name_collection=request.POST['newCollection'],last_update_time=datetime.datetime.now(),
        # delta_sending_time=interval_sec,format = request.POST['format'],subject=request.POST['format'])
    c = Collection(user=user,name_collection=request.POST['newCollection'],last_update_time=datetime.datetime.now(),
                   delta_sending_time=interval_sec,format=request.POST['format'],subject=request.POST['subject'])
    c.save()

    response.write("Success")
    return HttpResponse(response)

@user_passes_test(isAuth,login_url="/accounts/login/")
def deleteCollection(request):
    if request.POST.get('nameCollection') is None:
        return HttpResponse("error")
    user = Account.objects.get(username=request.user.username)
    collection = Collection.objects.get(user=user,name_collection=request.POST['nameCollection'])
    rss = RSSFeed.objects.filter(collection=collection)
    #for _rss in rss:

    collection.delete()

    return HttpResponse("Success")

@user_passes_test(isAuth,login_url="/accounts/login/")
def deleteRSS(request):
    if (request.POST.get("URL") is None) or (request.POST.get('nameCollection') is None):
        return HttpResponse("error");
    user = Account.objects.get(username=request.user.username)
    collection = Collection.objects.get(user=user,name_collection=request.POST['nameCollection'])
    rss = RSSFeed.objects.get(collection=collection,url=request.POST.get('URL'))
    if rss.collection.all().__len__()>1:
        rss.collection.remove(collection)
    else:
        rss.delete()
    return HttpResponse("Success")

@user_passes_test(isAuth,login_url="/accounts/login/")
def addRSS(request):
    if request.POST.get('nameOfNewRSS') is None:
        return HttpResponse("Empty field nameOfNewRSS")
    response = HttpResponse()
    response['Content-Type'] = "text/javascript"
    if isRss(request.POST.get('nameOfNewRSS'))==True:
        user = Account.objects.get(username=request.user.username)
        collection = Collection.objects.get(user=user,name_collection=request.POST['nameCollection'])
        if RSSFeed.objects.filter(collection=collection,url=request.POST.get('nameOfNewRSS')).__len__()>0:
            response.write("The RSS with such url already exists")
            return HttpResponse(response)
        if RSSFeed.objects.filter(url=request.POST.get('nameOfNewRSS')).__len__()>0:
            rss = RSSFeed.objects.get(url=request.POST.get('nameOfNewRSS'))
            rss.collection.add(collection)
            rss.save()
            return HttpResponse("Success")
        else:
            t = datetime.datetime.now()
            newRSS = RSSFeed(url=request.POST.get('nameOfNewRSS'),pubDate=t.strftime("%Y-%m-%d %H:%M:%S"),spoiled=False)
            newRSS.save()
            newRSS.collection.add(collection)
            newRSS.save()
            response.write("Success")
            return HttpResponse(response)
    else:
        response.write("This address doesn't belong to RSS")
        return HttpResponse(response)

@user_passes_test(isAuth,login_url="/accounts/login/")
def infoAboutCollection(request):
    if request.method=='POST':
        user = Account.objects.get(username=request.user.username)
        c = Collection.objects.filter(user=user,name_collection=request.POST['oldName'])
        mimetype = 'application/javascript'
        data = serializers.serialize('json', c)
        return HttpResponse(data,mimetype)
    return HttpResponse("Error/ No get")

@user_passes_test(isAuth,login_url="/accounts/login/")
def editCollection(request):
    if request.method=='POST':
        user = Account.objects.get(username = request.user.username)
        if Collection.objects.filter(user=user,name_collection = request.POST['newCollection']).__len__()>0 and request.POST['oldName']!=request.POST['newCollection']:
            return HttpResponse("Error/this name already exist")
        else:
            c = Collection.objects.get(user=user,name_collection=request.POST['oldName'])
            c.name_collection = request.POST['newCollection']
            c.format = request.POST['format']
            c.subject = request.POST['subject']
            # c.sendingTime = datetime.time(int(request.POST['sendingTime']))
            delta_sending_time = request.POST['delta_sending_time'];
            interval_sec = 0
            if "min" in delta_sending_time:
                interval_sec = int(delta_sending_time[:string.find(delta_sending_time,"min")])*60
            elif "h" in delta_sending_time:
                interval_sec = int(delta_sending_time[:string.find(delta_sending_time,"h")]) * 60 * 60
            else:
                interval_sec = int(delta_sending_time[:string.find(delta_sending_time,"h")]) * 60 * 60 * 24
            c.delta_sending_time = interval_sec
            c.save()
            return HttpResponse("Success")
    else:
        return HttpResponse("Error/No get")




def resetPassword(request):
    if request.method == 'POST':
        form = ResetPassword(request.POST)
        if form.is_valid() is False:
            return render_to_response('registration/resetPassword.html',{'form':form},context_instance=RequestContext(request))
        salt = sha_constructor(str(random.random())).hexdigest()[:5]
        username = request.user.username
        email = request.POST['email']
        activation_key = sha_constructor(salt+email).hexdigest()
        subject = "NovaJoy: Reset password"
        ctx_dict = {'activation_key': activation_key,                 }
        message = render_to_string('registration/reset_password_email.txt',
                                   ctx_dict)
        post_letters = PostLetters(target=email,title=subject,body=message)
        post_letters.save()
        user = Account.objects.get(email=email)
        user.resetKey = activation_key
        user.save()

        return  render_to_response('registration/resetPassword_email.html',context_instance=RequestContext(request))
    form = ResetPassword()
    return render_to_response('registration/resetPassword.html',{'form':form},context_instance=RequestContext(request))

def resetPasswordConfirm(request,activation_key):
    if request.method == 'POST':
        form = Password(request.POST)
        if form.is_valid() is False:
            return render_to_response('registration/newPassword.html',{'form':form,'username':request.POST['username']},context_instance=RequestContext(request))
        user = Account.objects.get(username = request.POST['username'])
        user.set_password(request.POST['password1'])
        user.resetKey = None
        user.save()
        return render_to_response('registration/resetPassword_done.html',context_instance=RequestContext(request))
    else:
        try:
            user = Account.objects.get(resetKey=activation_key)
            form = Password()
            return render_to_response('registration/newPassword.html',{'form':form,'username':user.username},context_instance=RequestContext(request))
            # return HttpResponseRedirect('/accounts/passwordReset/new_password')
            #return HttpResponse("OK")
        except Account.DoesNotExist:
            return HttpResponse("Error")

def about(request):
    if not request.user.is_authenticated() or request.user.username=='novajoyUser':
        return render_to_response('about_carousel.html',{'templ':'registration/bar1.html'},
                                  context_instance=RequestContext(request))
    else:
        user = Account.objects.get(username=request.user.username)
        return render_to_response('about_carousel.html',{'templ':'registration/bar2.html','user_name':user.username},
                                  context_instance=RequestContext(request))

def contact(request):
    if not request.user.is_authenticated() or request.user.username=='novajoyUser':
        return render_to_response('contact.html',{'templ':'registration/bar1.html'},
                                  context_instance=RequestContext(request))
    else:
        return render_to_response('contact.html',{'templ':'registration/bar2.html','user_name':request.user.username},
                                  context_instance=RequestContext(request))

@user_passes_test(isAuth,login_url="/accounts/login/")
def changedPassword(request):
    user = Account.objects.get(username=request.user.username)
    if user.check_password(request.POST['oldPassword']) == True:
        user.set_password(request.POST['newPassword'])
        user.save()
        return HttpResponse("Success")
    return HttpResponse("Error")