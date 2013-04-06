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

def isAuth(user):
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
        return render_to_response('mainPage.html',{'collection':collection},context_instance=RequestContext(request))
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

@user_passes_test(isAuth,login_url="/accounts/login/")
def addRSS(request):
    if request.POST.get('nameOfNewRSS') is None:
        return HttpResponse("Empty field nameOfNewRSS")
    response = HttpResponse()
    response['Content-Type'] = "text/javascript"
    if isRss(request.POST.get('nameOfNewRSS'))==True:
        user = Account.objects.get(username=request.user.username)
        collection = Collection.objects.get(user=user,name_collection=request.POST['nameCollection'])
        newRSS = RSSFeed(url=request.POST.get('nameOfNewRSS'),pubDate='2013-03-31 13:10:32')
        newRSS.save()
        newRSS.collection.add(collection)
        newRSS.save()
        response.write("Success")
        return HttpResponse(response)
    else:
        response.write("Error")
        return HttpResponse(response)

#@user_passes_test(isAuth,login_url="/accounts/login/")
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


