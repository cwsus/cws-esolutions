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

function showReverseDisclaimer(theOption)
{
    var i = 0;

    for (i = theOption.options.length - 1; i >= 0; i--)
    {
        if (theOption.selectedIndex.value == 'PTR')
        {
            document.getElementById('validationError').innerHTML = 'Please note that some services do not provide reverse entries.';
        }

        break;
    }
}

// Validate form contents
function validateForm(theForm, e)
{
    var targ;
    var i = 0;
    var matchString = /^[0-9a-zA-Z]+$/;

    // TODO: make sure this excludes password and email addr fields
    for (i = 0; i < theForm.elements.length; i++)
    {
        if ((theForm.elements[i].type == 'text') || (theForm.elements[i].type == 'password') && (theForm.elements[i].value != ""))
        {
            if (!(matchString.test(theForm.elements[i].value)) && (!("messageFrom".indexOf(theForm.elements[i].name) !== 1)))
            {
                document.getElementById('validationError').innerHTML = 'The value provided is not valid.';
                theForm.elements[i].style.color = '#FF0000';
                theForm.elements[i].style.color = '#FF0000';
                document.getElementById('execute').disabled = false;
                theForm.elements[i].focus();

                return false;
            }
        }
    }

    if (!(e))
    {
        e = window.event;
    }

    if (e.target)
    {
        targ = e.target;
    }
    else if (e.srcElement)
    {
        targ = e.srcElement;
    }

    if (targ.nodeType == 3) // defeat Safari bug
    {
        targ = targ.parentNode;
    }

    if (theForm.name == 'submitCombinedLogin')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                clearForm();

                document.getElementById('username').focus();
            }
            else
            {
                if (theForm.loginUser.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'Your username must be provided.';
                    document.getElementById('txtUsername').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('username').focus();
                }
                else if (theForm.loginPass.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'Your password must be provided.';
                    document.getElementById('txtPassword').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('username').focus();
                }
                else
                {
                    theForm.submit();
                }
            }
        }
    }
    else if (theForm.name == 'submitUserLogin')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                clearForm();

                document.getElementById('username').focus();
            }
            else
            {
                if (theForm.username.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'Your username must be provided.';
                    document.getElementById('txtUsername').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('username').focus();
                }
                else
                {
                    theForm.submit();
                }
            }
        }
    }
    else if (theForm.name == 'submitPassword')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                clearForm();

                history.go(-1);
            }
            else
            {
                if (theForm.password.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'Your username must be provided.';
                    document.getElementById('txtPassword').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('password').focus();
                }
                else
                {
                    theForm.submit();
                }
            }
        }
    }
    else if (theForm.name == 'submitOTP')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                history.go(-1);
            }
            else
            {
                if (theForm.password.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'Your username must be provided.';
                    document.getElementById('txtPassword').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('password').focus();
                }
                else if (theForm.otpValue.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'Your username must be provided.';
                    document.getElementById('txtOtpValue').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('otpValue').focus();
                }
                else
                {
                    theForm.submit();
                }
            }
        }
    }
    else if (theForm.name == 'submitEmailForUserSearch')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                history.go(-1);
            }
            else
            {
                if (theForm.emailAddr.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'Your email address must be provided.';
                    document.getElementById('txtEmailAddr').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('emailAddr').focus();
                }
                else
                {
                    theForm.submit();
                }
            }
        }
    }
    else if (theForm.name == 'submitUsernameForSearch')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                history.go(-1);
            }
            else
            {
                if (theForm.username.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'You must provide your account username.';
                    document.getElementById('txtUsername').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('username').focus();
                }
                else
                {
                    theForm.submit();
                }
            }
        }
    }
    else if (theForm.name == 'submitSecurityQuestion')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                history.go(-1);
            }
            else
            {
                if (theForm.secAnswerOne.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'You must provide your account username.';
                    document.getElementById('txtAnswerOne').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('secAnswerOne').focus();
                }
                else if (theForm.secAnswerTwo.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'You must provide your account username.';
                    document.getElementById('txtAnswerTwo').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('secAnswerOne').focus();
                }
                else
                {
                    theForm.submit();
                }
            }
        }
    }
    else if (theForm.name == 'submitNameLookup')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                history.go(-1);
            }
            else
            {
                if (theForm.recordName.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'You must provide a hostname or IP address to perform a query against.';
                    document.getElementById('txtServiceName').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('recordName').focus();
                }
                else
                {
                    theForm.submit();
                }
            }
        }
    }
    else if (theForm.name == 'submitArticleSearch')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                history.go(-1);
            }
            else
            {
                if (theForm.searchTerms.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'You must provide a search value.';
                    document.getElementById('txtSearchTerms').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('searchTerms').focus();
                }
                else
                {
                    theForm.submit();
                }
            }
        }
    }
    else if (theForm.name == 'submitNewArticle')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                history.go(-1);
            }
            else
            {
                if (theForm.title.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'The new article must have a title.';
                    document.getElementById('txtArticleTitle').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('title').focus();
                }
                else if (theForm.symptoms.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'The new article must have associated symptoms.';
                    document.getElementById('txtArticleSymptoms').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('symptoms').focus();
                }
                else if (theForm.cause.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'The new article must have an associated cause.';
                    document.getElementById('txtArticleCause').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('cause').focus();
                }
                else if (theForm.keywords.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'The new article must have searchable keywords.';
                    document.getElementById('txtArticleKeywords').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('keywords').focus();
                }
                else if (theForm.resolution.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'The new article must have an associated resolution.';
                    document.getElementById('txtArticleResolution').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('resolution').focus();
                }
                else
                {
                    theForm.submit();
                }
            }
        }
    }
    else if (theForm.name == 'submitSecurityInformationChange')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                history.go(-1);
            }
            else
            {
                if (theForm.secQuestionOne.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'A security question must be selected.';
                    document.getElementById('txtQuestionOne').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('secQuestionOne').focus();
                }
                else if (theForm.secQuestionTwo.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'A security question must be selected.';
                    document.getElementById('txtQuestionTwo').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('secQuestionOne').focus();
                }
                if (theForm.secAnswerOne.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'A security answer must be selected.';
                    document.getElementById('txtAnswerOne').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('secQuestionOne').focus();
                }
                else if (theForm.secAnswerTwo.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'A security answer must be selected.';
                    document.getElementById('txtAnswerTwo').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('secQuestionOne').focus();
                }
                else if (theForm.currentPassword.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'Your current password must be provided.';
                    document.getElementById('txtPassword').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('secQuestionOne').focus();
                }
                else
                {
                    if (theForm.secQuestionOne.value == theForm.secQuestionTwo.value) // make sure the questions arent the same
                    {
                        clearText(theForm);

                        document.getElementById('validationError').innerHTML = 'The security questions must be different.';
                        document.getElementById('txtQuestionOne').style.color = '#FF0000';
                        document.getElementById('execute').disabled = false;
                        document.getElementById('secQuestionOne').focus();
                    }
                    else if (theForm.secAnswerOne.value == theForm.secAnswerTwo.value) // make sure the answers arent the same
                    {
                        clearText(theForm);

                        document.getElementById('validationError').innerHTML = 'The security answers must be different.';
                        document.getElementById('txtQuestionOne').style.color = '#FF0000';
                        document.getElementById('execute').disabled = false;
                        document.getElementById('secQuestionOne').focus();
                    }
                    else if (theForm.secQuestionOne.value == theForm.secAnswerOne.value) // make sure question 1 doesnt match answer 1
                    {
                        clearText(theForm);

                        document.getElementById('validationError').innerHTML = 'A security question must be selected.';
                        document.getElementById('txtQuestionTwo').style.color = '#FF0000';
                        document.getElementById('execute').disabled = false;
                        document.getElementById('secQuestionOne').focus();
                    }
                    else if (theForm.secQuestionTwo.value == theForm.secAnswerTwo.value) // make sure question 2 doesnt match answer 2
                    {
                        clearText(theForm);

                        document.getElementById('validationError').innerHTML = 'A security question must be selected.';
                        document.getElementById('txtQuestionTwo').style.color = '#FF0000';
                        document.getElementById('execute').disabled = false;
                        document.getElementById('secQuestionOne').focus();
                    }
                    else if (theForm.secQuestionOne.value == theForm.secAnswerTwo.value) // make sure question 1 doesnt match answer 2
                    {
                        clearText(theForm);

                        document.getElementById('validationError').innerHTML = 'A security question must be selected.';
                        document.getElementById('txtQuestionTwo').style.color = '#FF0000';
                        document.getElementById('execute').disabled = false;
                        document.getElementById('secQuestionOne').focus();
                    }
                    else if (theForm.secQuestionTwo.value == theForm.secAnswerOne.value)
                    {
                        clearText(theForm);

                        document.getElementById('validationError').innerHTML = 'A security question must be selected.';
                        document.getElementById('txtQuestionTwo').style.color = '#FF0000';
                        document.getElementById('execute').disabled = false;
                        document.getElementById('secQuestionOne').focus();
                    }
                    else
                    {
                        theForm.submit();
                    }
                }
            }
        }
    }
    else if (theForm.name == 'submitEmailChange')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                history.go(-1);
            }
            else
            {
                if (theForm.emailAddr.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'You must provide your new email address.';
                    document.getElementById('txtEmailAddr').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('emailAddr').focus();
                }
                if (theForm.currentPassword.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'You must provide your password.';
                    document.getElementById('txtPassword').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('emailAddr').focus();
                }
                else
                {
                    if (checkEmailAddr(theForm.emailAddr.value))
                    {
                        theForm.submit();
                    }
                    else
                    {
                        clearText(theForm);

                        document.getElementById('validationError').innerHTML = 'Your email address did not pass validation. Please provide a new email address.';
                        document.getElementById('txtEmailAddr').style.color = '#FF0000';
                        document.getElementById('execute').disabled = false;
                        document.getElementById('emailAddr').focus();
                    }
                }
            }
        }
    }
    else if (theForm.name == 'createNewServer')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                history.go(-1);
            }
            else
            {
                if (theForm.osName.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'An OS type must be provided.';
                    document.getElementById('txtOsName').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('osName').focus();
                }
                else if (theForm.operHostName.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'A system hostname must be provided.';
                    document.getElementById('txtOperHostname').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('osName').focus();
                }
                else if (theForm.operIpAddress.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'A system IP address must be provided.';
                    document.getElementById('txtOperAddress').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('osName').focus();
                }
                else
                {
                    if ((theForm.serverType.value == 'DMGR') || (theForm.serverType.value == 'VIRTUALHOST'))
                    {
                        if (theForm.serverType.value == 'DMGR') 
                        {
                            if ((theForm.dmgrPort.value == '') || (theForm.dmgrPort.value == 0))
                            {
                                clearText(theForm);

                                document.getElementById('validationError').innerHTML = 'Server type specified as Deployment Manager but no service port was provided.';
                                document.getElementById('txtDmgrPort').style.color = '#FF0000';
                                document.getElementById("dmgrPort").style.display = 'block';
                                document.getElementById("mgrUrl").style.display = 'none';
                                document.getElementById('serverType').selected.value = 'DMGR';
                                document.getElementById('execute').disabled = false;
                                document.getElementById('osName').focus();
                            }
                            else if (theForm.mgrUrl.value == '')
                            {
                                clearText(theForm);

                                document.getElementById('validationError').innerHTML = 'Server type specified as Deployment Manager but no manager URL was provided.';
                                document.getElementById('txtDmgrPort').style.color = '#FF0000';
                                document.getElementById("dmgrPort").style.display = 'block';
                                document.getElementById("mgrUrl").style.display = 'none';
                                document.getElementById('serverType').selected.value = 'DMGR';
                                document.getElementById('execute').disabled = false;
                                document.getElementById('osName').focus();
                            }
                            else
                            {
                                theForm.submit();
                            }
                        }
                        else if (theForm.serverType.value == 'VIRTUALHOST')
                        {
                            if (theForm.mgrUrl.value == '')
                            {
                                clearText(theForm);

                                document.getElementById('validationError').innerHTML = 'Server type specified as Virtual Manager but no manager URL was provided.';
                                document.getElementById('txtDmgrPort').style.color = '#FF0000';
                                document.getElementById("dmgrPort").style.display = 'block';
                                document.getElementById("mgrUrl").style.display = 'none';
                                document.getElementById('serverType').selected.value = 'VIRTUALHOST';
                                document.getElementById('execute').disabled = false;
                                document.getElementById('osName').focus();
                            }
                            else
                            {
                                theForm.submit();
                            }
                        }
                        else
                        {
                            theForm.submit();
                        }
                    }
                    else
                    {
                        theForm.submit();
                    }
                }
            }
        }
    }
    else if (theForm.name == 'submitRemoteDate')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                history.go(-1);
            }
            else
            {
                if (theForm.targetServer.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'A target host must be provided.';
                    document.getElementById('txtTargetHostName').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('targetServer').focus();
                }
                else
                {
                    theForm.submit();
                }
            }
        }
    }
    else if (theForm.name == 'submitTelnetRequest')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                history.go(-1);
            }
            else
            {
                if (theForm.targetServer.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'A target host must be provided.';
                    document.getElementById('txtTargetHostName').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('targetServer').focus();
                }
                else if ((theForm.targetPort.value == '') || (isNaN(theForm.targetPort.value)))
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'A target port number must be provided.';
                    document.getElementById('txtTargetPort').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('targetServer').focus();
                }
                else
                {
                    theForm.submit();
                }
            }
        }
    }
    else if (theForm.name == 'createNewProject')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                history.go(-1);
            }
            else
            {
                if (theForm.projectCode.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'A project code must be provided.';
                    document.getElementById('txtProjectCode').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('projectCode').focus();
                }
                else if (theForm.projectStatus.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'A project status must be provided.';
                    document.getElementById('txtProjectStatus').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('projectCode').focus();
                }
                else if (theForm.primaryContact.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'A project contact must be provided.';
                    document.getElementById('txtPrimaryContact').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('projectCode').focus();
                }
                else if (theForm.contactEmail.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'A contact email must be provided.';
                    document.getElementById('txtContactEmail').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('projectCode').focus();
                }
                else if (theForm.changeQueue.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'A change queue must be provided.';
                    document.getElementById('txtChangeQueue').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('projectCode').focus();
                }
                else if (theForm.incidentQueue.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'An incident queue must be provided.';
                    document.getElementById('txtIncidentQueue').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('projectCode').focus();
                }
                else
                {
                    theForm.submit();
                }
            }
        }
    }
    else if (theForm.name == 'createNewApplication')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                history.go(-1);
            }
            else
            {
                if (theForm.applicationName.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'An application name must be provided.';
                    document.getElementById('txtApplicationName').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('applicationName').focus();
                }
                else if (theForm.version.value == '')
                {
                    // default to 1.0
                    document.getElementById('applicationName').value = '1.0';
                }
                else if (theForm.clusterName.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'A target application cluster must be provided.';
                    document.getElementById('txtApplicationCluster').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('applicationName').focus();
                }
                else if (theForm.project.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'The application must be associated with a project.';
                    document.getElementById('txtApplicationProject').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('applicationName').focus();
                }
                else if (theForm.platform.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'The application must be associated with a platform.';
                    document.getElementById('txtApplicationPlatform').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('applicationPlatform').focus();
                }
                else if (theForm.logsPath.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'A valid path must be provided for application logs.';
                    document.getElementById('txtApplicationLogsPath').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('applicationName').focus();
                }
                else if (theForm.installPath.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'A valid path for application binaries must be provided.';
                    document.getElementById('txtApplicationInstallPath').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('applicationName').focus();
                }
                else
                {
                    theForm.submit();
                }
            }
        }
    }
    else if (theForm.name == 'selectDmgr')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                history.go(-1);
            }
            else
            {
                if (theForm.serverGuid.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'A deployment manager must be provided.';
                    document.getElementById('txtPlatformDmgr').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('serverGuid').focus();
                }
                else
                {
                    theForm.submit();
                }
            }
        }
    }
    else if (theForm.name == 'createNewPlatform')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                history.go(-1);
            }
            else
            {
                if (theForm.platformName.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'A platform name must be provided.';
                    document.getElementById('txtPlatformName').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('platformName').focus();
                }
                else if (theForm.status.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'A platform status must be provided.';
                    document.getElementById('txtPlatformStatus').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('platformName').focus();
                }
                else if (theForm.appServers.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'Target application servers must be provided.';
                    document.getElementById('txtPlatformAppservers').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('platformName').focus();
                }
                else if (theForm.webServers.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'Target web servers must be provided.';
                    document.getElementById('txtPlatformWebservers').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('platformName').focus();
                }
                else
                {
                    theForm.submit();
                }
            }
        }
    }
    else if (theForm.name == 'searchRequest')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                history.go(-1);
            }
            else
            {
                if (theForm.searchTerms.value = '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'Search terms must be provided.';
                    document.getElementById('txtSearchTerms').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('searchTerms').focus();
                }
                else
                {
                    theForm.submit();
                }
            }
        }
    }
    else if (theForm.name == 'submitContactForm')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                history.go(-1);
            }
            else
            {
                if (theForm.messageSubject.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'Please provide a brief subject for your request.';
                    document.getElementById('txtMessageSubject').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('messageSubject').focus();
                }
                else if (theForm.messageBody.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'Please provide the information regarding your request.';
                    document.getElementById('txtMessageBody').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('messageSubject').focus();
                }
                else
                {
                    theForm.submit();
                }
            }
        }
    }
    else if (theForm.name == 'submitContactChange')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                history.go(-1);
            }
            else
            {
                if (theForm.telNumber.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'Please provide a contact telephone number.';
                    document.getElementById('txtTelNumber').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('telNumber').focus();
                }
                else if (theForm.pagerNumber.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'Please provide a notification pager number.';
                    document.getElementById('txtPagerNumber').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('pagerNumber').focus();
                }
                else
                {
                    theForm.submit();
                }
            }
        }
    }
    else if (theForm.name == 'deployApplication')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                history.go(-1);
            }
            else
            {
                if (!((theForm.deploymentType1.checked)) && (!(theForm.deploymentType2.checked)))
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'Please provide the deployment type.';
                    document.getElementById('txtDeploymentType').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                }
                else if ((theForm.version.value == '') || (theForm.version.value == '0.0'))
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'Please provide the software version to deploy.';
                    document.getElementById('txtScmVersion').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('version').focus();
                }
                else if ((theForm.file) && (theForm.file.value == ''))
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'Please provide the file to deploy.';
                    document.getElementById('txtMessageSubject').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('messageSubject').focus();
                }
                else
                {
                    theForm.submit();
                }
            }
        }
    }
    else if (theForm.name == 'searchUserAccounts')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                history.go(-1);
            }
            else
            {
                var i = 0;
                var f = 0;
                var entryFound = false;

                for (f = 0; f < document.forms.length; f++)
                {
                    // for each element in each form
                    for (i = 0; i < document.forms[f].length; i++)
                    {
                        // if it's not a hidden element, disabled or a button
                        if ((document.forms[f][i].type != "hidden") && (document.forms[f][i].disabled != true) && (document.forms[f][i].type != "button"))
                        {
                            // clear it
                            if (document.forms[f][i].value != '')
                            {
                                entryFound = true;

                                break;
                            }
                        }
                    }
                }

                if (entryFound)
                {
                    theForm.submit();
                }
                else
                {
                    document.getElementById('validationError').innerHTML = 'A search criterion must be provided.';
                    document.getElementById('txtUserName').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('username').focus();
                }
            }
        }
    }
    else if (theForm.name == 'createNewDatacenter')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                history.go(-1);
            }
            else
            {
                if (theForm.datacenterName.value == '')
                {
                    document.getElementById('validationError').innerHTML = 'A name must be provided for the new datacenter.';
                    document.getElementById('txtDatacenterName').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('datacenterName').focus();
                }
                else if (theForm.datacenterStatus.value == '')
                {
                    document.getElementById('validationError').innerHTML = 'A datacenter status must be provided.';
                    document.getElementById('txtDatacenterStatus').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('datacenterStatus').focus();
                }
                else if (theForm.datacenterDesc.value == '')
                {
                    document.getElementById('validationError').innerHTML = 'A description of the datacenter must be provided.';
                    document.getElementById('txtDatacenterDescription').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('datacenterDesc').focus();
                }
                else
                {
                    theForm.submit();
                }
            }
        }
    }
    else if (theForm.name == 'submitPasswordChange')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                history.go(-1);
            }
            else
            {
                if ((theForm.currentPassword) && (theForm.currentPassword.value == ''))
                {
                    document.getElementById('validationError').innerHTML = 'You must provide your current password.';
                    document.getElementById('txtCurrentPassword').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('currentPassword').focus();
                }
                else if (theForm.newPassword.value == '')
                {
                    document.getElementById('validationError').innerHTML = 'You must provide your new password.';
                    document.getElementById('txtNewPassword').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('newPassword').focus();
                }
                else if (theForm.confirmPassword.value == '')
                {
                    document.getElementById('validationError').innerHTML = 'Your new password must be confirmed.';
                    document.getElementById('txtConfirmPassword').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('confirmPassword').focus();
                }
                else
                {
                    if ((theForm.currentPassword) && (theForm.currentPassword.value == theForm.newPassword.value))
                    {
                        clearForm();

                        document.getElementById('validationError').innerHTML = 'Your new password cannot match your existing password.';
                        document.getElementById('execute').disabled = false;
                        document.getElementById('currentPassword').focus();
                    }
                    else if ((theForm.currentPassword) && (theForm.currentPassword.value == theForm.confirmPassword.value))
                    {
                        clearForm();

                        document.getElementById('validationError').innerHTML = 'Your new password cannot match your existing password.';
                        document.getElementById('execute').disabled = false;
                        document.getElementById('currentPassword').focus();
                    }
                    else if (theForm.newPassword.value != theForm.confirmPassword.value)
                    {
                        clearForm();

                        document.getElementById('validationError').innerHTML = 'Your new and confirmed passwords must match.';
                        document.getElementById('execute').disabled = false;
                        document.getElementById('currentPassword').focus();
                    }
                    else
                    {
                        theForm.submit();
                    }
                }
            }
        }
    }
    else if (theForm.name == 'submitSystemMessage')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                history.go(-1);
            }
            else
            {
                if (theForm.messageTitle.value == '')
                {
                    document.getElementById('validationError').innerHTML = 'A message subject must be provided.';
                    document.getElementById('txtSysMessageSubject').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('messageTitle').focus();
                }
                else if (theForm.messageText.value == '')
                {
                    document.getElementById('validationError').innerHTML = 'A message body must be provided.';
                    document.getElementById('txtSysMessageBody').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('datacenterStatus').focus();
                }
                else if (!((theForm.isActive1.checked)) && (!(theForm.isActive2.checked)))
                {
                    document.getElementById('validationError').innerHTML = 'A message status must be provided.';
                    document.getElementById('txtIsMessageActive').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                }
                else
                {
                    theForm.submit();
                }
            }
        }
    }
    else if (theForm.name == 'createNewUser')
    {
        if ((e.keyCode == 13) || (e.type == 'click'))
        {
            if (targ.id == 'cancel')
            {
                history.go(-1);
            }
            else
            {
                if (theForm.username.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'Please provide a brief subject for your request.';
                    document.getElementById('"txtUsername"').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('username').focus();
                }
                else if (theForm.role.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'Please provide the information regarding your request.';
                    document.getElementById('txtUserRole').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('role').focus();
                }
                else if (theForm.givenName.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'Please provide the information regarding your request.';
                    document.getElementById('txtFirstName').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('givenName').focus();
                }
                else if (theForm.surname.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'Please provide the information regarding your request.';
                    document.getElementById('txtLastName').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('surname').focus();
                }
                else if (theForm.emailAddr.value == '')
                {
                    clearText(theForm);

                    document.getElementById('validationError').innerHTML = 'Please provide the information regarding your request.';
                    document.getElementById('txtEmailAddr').style.color = '#FF0000';
                    document.getElementById('execute').disabled = false;
                    document.getElementById('emailAddr').focus();
                }
                else
                {
                    theForm.submit();
                }
            }
        }
    }
    else
    {
        document.getElementById('validationError').innerHTML = 'An error occurred while processing your request.';
        document.getElementById('execute').disabled = false;
    }
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
