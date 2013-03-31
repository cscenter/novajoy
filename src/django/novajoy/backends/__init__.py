# -*- coding: utf-8 -*-

from django.contrib.sites.models import RequestSite, Site
from registration.backends.default import DefaultBackend
from registration.models import RegistrationProfile
from Server.models import Account,PostLetters
from django.template.loader import render_to_string
from django.conf import settings


# Registration Backend 
class RegBackend(DefaultBackend):
    def register(self, request, **kwargs):
        username, email, password,age= kwargs['username'], kwargs['email'], kwargs['password1'],kwargs['age']
        if Site._meta.installed:
            site = Site.objects.get_current()
        else:
            site = RequestSite(request)

        user = Account.objects.create_user(username, email, password,age)
        user.is_active = False
        user.save()
        profile = RegistrationProfile.objects.create_profile(user)
        ctx_dict = {'activation_key': profile.activation_key,
                    'expiration_days': settings.ACCOUNT_ACTIVATION_DAYS,
                    'site': site}
        subject = render_to_string('registration/activation_email_subject.txt',
                                   ctx_dict)
        subject = ''.join(subject.splitlines())
		# write message for DB PostLetters
        message = render_to_string('registration/activation_email.txt',
                                   ctx_dict)
        post_letters = PostLetters(target=profile.user.email,title=subject,body=message)
        post_letters.save()
        return profile
