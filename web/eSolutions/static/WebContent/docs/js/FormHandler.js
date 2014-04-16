// Disable button
function disableButton(theButton)
{
    theButton.disabled=true;
}

// Clear all textboxes on a form
function clearText(theForm)
{
    var i = 0;

    for (i = 0; i < theForm.elements.length; i++)
    {
        if ((theForm.elements[i].type == 'text') || (theForm.elements[i].type == 'password'))
        {
            theForm.elements[i].value = '';
        }
    }
}

//Clear text boxes
function doClear(theText)
{
    if (theText.value == theText.defaultValue)
    {
        theText.value = "";
    }
}

// Import Script js - show/hide options
function selectOption(theOption)
{
    var i = 0;
    var showClassName = "show";
    var hideClassName = "hide";
    var selectedValue = theOption.options[theOption.selectedIndex].value;
    var divName = document.getElementById(selectedValue);

    for (i = theOption.options.length - 1; i >= 0; i--)
    {
        document.getElementById(selObj.options[i].value).className = hideClassName;
    }

    divName.className = showClassName;
}

// clear form
function clearForm()
{
    var i = 0;
    var f = 0;

    for (f = 0; f < document.forms.length; f++)
    {
        // for each element in each form
        for (i = 0; i < document.forms[f].length; i++)
        {
            // if it's not a hidden element, disabled or a button
            if ((document.forms[f][i].type != "hidden") && (document.forms[f][i].disabled != true) && (document.forms[f][i].type != "button"))
            {
                // clear it
                document.forms[f][i].value = '';
            }
        }
    }
}

//Focus to first available field
window.onload=setFocus;

function setFocus()
{
    var f = 0;
    var i = 0;

    // for each form
    for (f=0; f < document.forms.length; f++)
    {
        // for each element in each form
        for(i=0; i < document.forms[f].length; i++)
        {
            // if it's not a hidden element and it's not disabled...
            if ((document.forms[f][i].type != "hidden") && (document.forms[f][i].disabled != true))
            {
                // ...set the focus to it
                document.forms[f][i].focus();

                break;
            }
        }
    }
}

function checkEmailAddr(theText)
{
    var sQtext = '[^\\x0d\\x22\\x5c\\x80-\\xff]';
    var sDtext = '[^\\x0d\\x5b-\\x5d\\x80-\\xff]';
    var sAtom = '[^\\x00-\\x20\\x22\\x28\\x29\\x2c\\x2e\\x3a-\\x3c\\x3e\\x40\\x5b-\\x5d\\x7f-\\xff]+';
    var sQuotedPair = '\\x5c[\\x00-\\x7f]';
    var sDomainLiteral = '\\x5b(' + sDtext + '|' + sQuotedPair + ')*\\x5d';
    var sQuotedString = '\\x22(' + sQtext + '|' + sQuotedPair + ')*\\x22';
    var sDomain_ref = sAtom;
    var sSubDomain = '(' + sDomain_ref + '|' + sDomainLiteral + ')';
    var sWord = '(' + sAtom + '|' + sQuotedString + ')';
    var sDomain = sSubDomain + '(\\x2e' + sSubDomain + ')*';
    var sLocalPart = sWord + '(\\x2e' + sWord + ')*';
    var sAddrSpec = sLocalPart + '\\x40' + sDomain; // complete RFC822 email address spec
    var sValidEmail = '^' + sAddrSpec + '$'; // as whole string

    var reValidEmail = new RegExp(sValidEmail);

    if (reValidEmail.test(theText))
    {
        return true;
    }

    return false;
}
