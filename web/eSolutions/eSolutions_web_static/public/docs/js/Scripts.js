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
    };

    xhr.send(null);
}

function resetForm()
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
        newWindow.focus();
    }

    return false;
}
