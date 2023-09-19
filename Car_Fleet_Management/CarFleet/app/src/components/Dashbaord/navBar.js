import * as React from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import Menu from '@mui/material/Menu';
import MenuIcon from '@mui/icons-material/Menu';
import Container from '@mui/material/Container';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import Tooltip from '@mui/material/Tooltip';
import MenuItem from '@mui/material/MenuItem';
import AdbIcon from '@mui/icons-material/Adb';
import GetAppIcon from '@mui/icons-material/GetApp';

import { Link } from 'react-router-dom';

const pages = [
  { label: 'Home', route: '/CarFleet/Dashboard' },
  { label: 'Cars', route: '/CarFleet/Cars' },
  { label: 'Locations', route: '/CarFleet/Locations' },
  { label: 'History', route: '/CarFleet/LocationsHistory' },
  { label: 'Users', route: '/CarFleet/Users' },
];

const settings = [
  { label: 'Account', route: '/CarFleet/Account/Profile' },
  { label: 'Logout', action: 'logout' },
];

function ResponsiveAppBar() {
  const [anchorElNav, setAnchorElNav] = React.useState(null);
  const [anchorElUser, setAnchorElUser] = React.useState(null);

  const handleOpenNavMenu = (event) => {
    setAnchorElNav(event.currentTarget);
  };
  const handleOpenUserMenu = (event) => {
    setAnchorElUser(event.currentTarget);
  };

  const handleCloseNavMenu = () => {
    setAnchorElNav(null);
  };

  const handleCloseUserMenu = () => {
    setAnchorElUser(null);
  };

  const handleSettingsAction = (action) => {
    if (action === 'logout') {
      // Remove the userData from local storage when the user clicks "Logout"
      localStorage.removeItem('userData');

      // Redirect the user to the login page or another appropriate page
      // Replace '/CarFleet/Login' with the appropriate route for the login page
      window.location.href = '/CarFleet/Login';
    }
    handleCloseUserMenu();
  };

  const userData = JSON.parse(localStorage.getItem('userData'));
  const fullName = userData ? `${userData.fname} ${userData.lname}` : 'Unknown User';

  return (
    <AppBar position="static">
      <Container maxWidth="xl">
        <Toolbar disableGutters>
          <AdbIcon sx={{ display: { xs: 'none', md: 'flex' }, mr: 1 }} />
          <Typography
            variant="h6"
            noWrap
            component={Link}
            to="/CarFleet/Dashboard"
            sx={{
              mr: 2,
              display: { xs: 'none', md: 'flex' },
              fontFamily: 'monospace',
              fontWeight: 700,
              letterSpacing: '.3rem',
              color: 'inherit',
              textDecoration: 'none',
            }}
          >
            CarFleet
          </Typography>

          <Box sx={{ flexGrow: 1, display: { xs: 'flex', md: 'none' } }}>
            <IconButton
              size="large"
              aria-label="account of current user"
              aria-controls="menu-appbar"
              aria-haspopup="true"
              onClick={handleOpenNavMenu}
              color="inherit"
            >
              <MenuIcon />
            </IconButton>
            <Menu
              id="menu-appbar"
              anchorEl={anchorElNav}
              anchorOrigin={{
                vertical: 'bottom',
                horizontal: 'left',
              }}
              keepMounted
              transformOrigin={{
                vertical: 'top',
                horizontal: 'left',
              }}
              open={Boolean(anchorElNav)}
              onClose={handleCloseNavMenu}
              sx={{
                display: { xs: 'block', md: 'none' },
              }}
            >
              {pages.map((page) => (
                <MenuItem key={page.label} onClick={handleCloseNavMenu}>
                  <Link
                    to={page.route}
                    style={{
                      color: 'inherit',
                      textDecoration: 'none',
                      display: 'block',
                      width: '100%',
                    }}
                  >
                    <Typography textAlign="center">{page.label}</Typography>
                  </Link>
                </MenuItem>
              ))}
            </Menu>
          </Box>
          <AdbIcon sx={{ display: { xs: 'flex', md: 'none' }, mr: 1 }} />
          <Typography
            variant="h5"
            noWrap
            component="a"
            href=""
            sx={{
              mr: 2,
              display: { xs: 'flex', md: 'none' },
              flexGrow: 1,
              fontFamily: 'monospace',
              fontWeight: 700,
              letterSpacing: '.3rem',
              color: 'inherit',
              textDecoration: 'none',
            }}
          >
            CarFleet
          </Typography>
          <Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' } }}>
            {pages.map((page) => (
              <Button
                key={page.label}
                component={Link}
                to={page.route}
                sx={{ my: 2, color: 'white', display: 'block' }}
              >
                {page.label}
              </Button>
            ))}
          </Box>

          {/* Download App Button */}
          <div style={{ marginLeft: '16px', marginTop: '8px', display: 'flex', alignItems: 'center' }}>
            <Button
              component="a"
              href="/CarFleetManagement.apk" // Update with the correct path
              target="_blank"
              rel="noopener noreferrer"
              sx={{
                color: 'white',
                display: 'flex',
                alignItems: 'center',
                textDecoration: 'none',
                borderRadius: '4px',
                padding: '8px 16px',
                transition: 'background-color 0.3s ease',
              }}
            >
              <GetAppIcon sx={{ marginRight: '8px' }} />
              <span style={{ fontWeight: 'bold' }}>Download App</span>
            </Button>
          </div>



          <Box sx={{ flexGrow: 0 }}>
            <Tooltip title={fullName}>
              <IconButton onClick={handleOpenUserMenu} sx={{ p: 0 }}>
                <Avatar alt={fullName} src="/static/images/avatar/2.jpg" />
              </IconButton>
            </Tooltip>
            <Menu
              sx={{ mt: '45px' }}
              id="menu-appbar"
              anchorEl={anchorElUser}
              anchorOrigin={{
                vertical: 'top',
                horizontal: 'right',
              }}
              keepMounted
              transformOrigin={{
                vertical: 'top',
                horizontal: 'right',
              }}
              open={Boolean(anchorElUser)}
              onClose={handleCloseUserMenu}
            >
              {settings.map((setting) => (
                <MenuItem
                  key={setting.label}
                  onClick={() => handleSettingsAction(setting.action)} // Pass the action to the handler
                >
                  <Link
                    to={setting.route}
                    style={{
                      color: 'inherit',
                      textDecoration: 'none',
                      display: 'block',
                      width: '100%',
                    }}
                  >
                    <Typography textAlign="center">{setting.label}</Typography>
                  </Link>
                </MenuItem>
              ))}
            </Menu>
          </Box>
        </Toolbar>
      </Container>
    </AppBar>
  );
}

export default ResponsiveAppBar;
