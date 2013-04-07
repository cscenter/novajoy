from django import forms
from captcha.fields import CaptchaField
from django.utils.translation import ugettext_lazy as _
from registration.forms import RegistrationForm
from django.core.validators import validate_email
from django.core.exceptions import ValidationError
from Server.models import Account
class CustomRegForm(RegistrationForm):
    age=forms.IntegerField(
        label=_('age'),
        )
    captcha = CaptchaField(label=_("Code in the image"))

class Password(forms.Form):

    password1 = forms.CharField(widget=forms.PasswordInput(render_value=False),
                                label=_("Password"))
    password2 = forms.CharField(widget=forms.PasswordInput(render_value=False),
                                label=_("Password (again)"))

    def clean(self):
        data = self.cleaned_data
        if "password1" in data and "password2" in data and data["password1"] != data["password2"]:
            # raise forms.ValidationError("Passwords must be same")
            msg = u"Passwords must be same"
            self._errors["password1"] = self.error_class([msg])
        return data

def validate_even(value):
    try:
        user = Account.objects.get(email=value)
    except Account.DoesNotExist:
        raise ValidationError(u' there is no user with such (%s) email' % value)

class ResetPassword(forms.Form):
    email = forms.CharField(validators=[validate_email,validate_even],
                            error_messages={'invalid': _(u'Enter a valid e-mail address.')})
    captcha = CaptchaField(label=_("Code in the image"))

