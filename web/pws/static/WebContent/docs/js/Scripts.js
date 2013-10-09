function getStatusCode()
{
    var xhr;
    var currLocation = window.location.pathname;

    try
    {
        xhr = new ActiveXObject('Msxml2.XMLHTTP');
    }
    catch (e)
    {
        try
        {
            xhr = new ActiveXObject('Microsoft.XMLHTTP');
        }
        catch (e2)
        {
            try
            {
                xhr = new XMLHttpRequest();
            }
            catch (e3)
            {
                xhr = false;
            }
        }
    }

    xhr.open("HEAD", currLocation, true);

    xhr.onreadystatechange = function()
    {
        if (xhr.readyState == 4)
        {
            document.getElementById('HttpStatusCode').innerHTML = "IFS0" + xhr.status;
        }
    }

    xhr.send(null);
}

// Clear that session
function clearSession()
{
    document.forms[0].reset();
}

// Popup window function
function popup(url, title, width, height, resize, stat, menu, scrl)
{
    var newWindow = window.open(url, title,
        'height=' + height + ', width=' + width + ', resize=' + resize + ', status=' + stat + ', menubar=' + menu + ', scrollbars=' + scrl);

    if (!newWindow.closed && newWindow.location)
    {
        newWindow.location.href = url;
    }
    else
    {
        if (!newWindow.opener)
        {
            newWindow.opener = self;
        }
    }
    if (window.focus)
    {
        newWindow.focus()
    }

    return false;
}

// Send redirect to login page
function returnLogin()
{
    window.location='/1/2/!ut/p/.scr/exec/Logon?doLogoff=true';
}

function confirmation()
{
    document.form.action = document.form.server_name.value + document.form.client_name.value;
    document.form.method ="POST";

    var confirmMsg = "Do you really want to send your test to \n" + document.form.action +"?\n";

    if (!confirm (confirmMsg))
    {
        document.form.action = "";
    }
}

function showReverseDisclaimer()
{
    if (document.getElementById('lookupType').value == 'PTR')
    {
        document.getElementById('validationError').innerHTML = "Note that some services do not provide reverse entries.";
        document.getElementById('validationError').style.color = '#FF0000';
    }
}

function showOptions()
{
    if (document.getElementById('type').value == 'Select..'
        || document.getElementById('type').value == '--')
    {
        document.getElementById('performSiteFailover').style.display = 'none';
        document.getElementById("performProjectFailover").style.display = 'none';
        document.getElementById("performBizFailover").style.display = 'none';
        document.getElementById("performDCFailover").style.display = 'none';
    }
    else if (document.getElementById('type').value == 's')
    {
        document.getElementById('performSiteFailover').style.display = 'block';
        document.getElementById("performProjectFailover").style.display = 'none';
        document.getElementById("performBizFailover").style.display = 'none';
        document.getElementById("performDCFailover").style.display = 'none';
    }
    else if (document.getElementById('type').value == 'p')
    {
        document.getElementById('performSiteFailover').style.display = 'none';
        document.getElementById("performProjectFailover").style.display = 'block';
        document.getElementById("performBizFailover").style.display = 'none';
        document.getElementById("performDCFailover").style.display = 'none';
    }
    else if (document.getElementById('type').value == 'b')
    {
        document.getElementById('performSiteFailover').style.display = 'none';
        document.getElementById("performProjectFailover").style.display = 'none';
        document.getElementById("performBizFailover").style.display = 'block';
        document.getElementById("performDCFailover").style.display = 'none';
    }
    else if (document.getElementById('type').value == 'd')
    {
        document.getElementById('performSiteFailover').style.display = 'none';
        document.getElementById("performProjectFailover").style.display = 'none';
        document.getElementById("performBizFailover").style.display = 'none';
        document.getElementById("performDCFailover").style.display = 'block';
    }
}

//Verify file is allowed
function testFileType(fileName, allowedType)
{
    var isValid = false;

    if (!!allowedType)
    {
        var fileTypes = ['tar', 'ear', 'war'];
    }

    if (!(fileName))
    {
        isValid = false;
    }
    else
    {
        dots = fileName.split(".");
        fileType = "." + dots[dots.length - 1];

        for (i = 0; i < fileTypes.length; i++)
        {
            if ("." + fileTypes[i] == fileType)
            {
                isValid = true;
                break;
            }
        }
    }

    if (isValid)
    {
        return true;
    }
    else
    {
        return fileTypes;
    }
}
