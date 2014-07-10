Summary:            A unified script application environment
Name:               UnifiedScriptLoader
Version:            1.0.0
Release:            1%{?dist}
License:            GPLv2+
Group:              Applications/System
Source:             %{_tmppath}/%{name}-%{version}.tar.gz
URL:                http://www.caspersbox.com/products/UnifiedScriptLoader
BuildArch:          noarch
Requires:           ksh
Requires:           expect
Requires:           perl
Requires:           pcre

%description
The Unified Script Loader allows a common environment for shell scripts to
be loaded and executed. It is a set of standards, variables and frameworks
that can be utilized across multiple script utilities.

This is best suited for large projects, although any script utility can be
added to it as a plugin with minimal work.

%prep
%autosetup -nc %{name}

%build

%install
rm -rf %{buildroot}
mkdir -pv %{buildroot}/%{_bindir}/%{name}
mkdir -pv %{buildroot}/%{_sysconfdir}/%{name}
mkdir -pv %{buildroot}/%{_libdir}/%{name}
mkdir -pv %{buildroot}/%{_libdir}/%{name}/tcl
mkdir -pv %{buildroot}/%{_libdir}/%{name}/validators
mkdir -pv %{buildroot}/%{_libdir}/%{name}/plugins

install -m 755 obtainChangeControl.sh %{buildroot}/%{_bindir}%{name}
install -m 755 serviceAdministrationUI.sh %{buildroot}/%{_bindir}%{name}
install -m 644 application.properties %{buildroot}/%{_sysconfdir}%{name}
install -m 644 errors.properties %{buildroot}/%{_sysconfdir}%{name}
install -m 644 resources.properties %{buildroot}/%{_sysconfdir}%{name}
install -m 644 logging.properties %{buildroot}/%{_sysconfdir}%{name}
install -m 755 generateEntropy.sh %{buildroot}/%{_libdir}%{name}
install -m 755 lock.sh %{buildroot}/%{_libdir}%{name}
install -m 755 logger.sh %{buildroot}/%{_libdir}%{name}
install -m 755 pushAdmin.sh %{buildroot}/%{_libdir}%{name}
install -m 755 runMonitor.sh %{buildroot}/%{_libdir}%{name}
install -m 755 sendNotification.sh %{buildroot}/%{_libdir}%{name}
install -m 755 systemBackup.sh %{buildroot}/%{_libdir}%{name}
install -m 755 validateSecurityAccess.sh %{buildroot}/%{_libdir}%{name}
install -m 755 watchdog.sh %{buildroot}/%{_libdir}%{name}
install -m 755 runSCPConnection.tcl %{buildroot}/%{_libdir}%{name}/tcl
install -m 755 runSSHConnection.tcl %{buildroot}/%{_libdir}%{name}/tcl
install -m 755 validateChangeTicket.sh %{buildroot}/%{_libdir}%{name}/validators
install -m 755 validatePortNumber.sh %{buildroot}/%{_libdir}%{name}/validators
install -m 755 validateTelephoneNumber.sh %{buildroot}/%{_libdir}%{name}/validators

%files
%{_bindir}/*
%{_libdir}/*
%{_libdir}/tcl/*
%{_libdir}/validators/*
%defattr(0755, root, root, 0755)
%attr(0755,root,root) %{_bindir}%{name}/obtainChangeControl.sh
%attr(0755,root,root) %{_bindir}%{name}/serviceAdministrationUI.sh
%attr(0755,root,root) %{_libdir}%{name}/generateEntropy.sh
%attr(0755,root,root) %{_libdir}%{name}/lock.sh
%attr(0755,root,root) %{_libdir}%{name}/logger.sh
%attr(0755,root,root) %{_libdir}%{name}/pushAdmin.sh
%attr(0755,root,root) %{_libdir}%{name}/runMonitor.sh
%attr(0755,root,root) %{_libdir}%{name}/sendNotification.sh
%attr(0755,root,root) %{_libdir}%{name}/systemBackup.sh
%attr(0755,root,root) %{_libdir}%{name}/validateSecurityAccess.sh
%attr(0755,root,root) %{_libdir}%{name}/watchdog.sh
%attr(0755,root,root) %{_libdir}%{name}/tcl/runSCPConnection.tcl
%attr(0755,root,root) %{_libdir}%{name}/tcl/runSSHConnection.tcl
%attr(0755,root,root) %{_libdir}%{name}/validators/validateChangeTicket.sh
%attr(0755,root,root) %{_libdir}%{name}/validators/validatePortNumber.sh
%attr(0755,root,root) %{_libdir}%{name}/validators/validateTelephoneNumber.sh
%attr(0644,root,root) %{_sysconfdir}/%{name}/application.properties
%attr(0644,root,root) %{_sysconfdir}/%{name}/errors.properties
%attr(0644,root,root) %{_sysconfdir}/%{name}/logging.properties
%attr(0644,root,root) %{_sysconfdir}/%{name}/resources.properties

%clean
rm -rf %{buildroot}

%changelog
