import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import './index.css';
import reportWebVitals from './reportWebVitals';

import Login from './components/SignIn';
import ForgotPassword from './components/ForgotPassword';
import Dashboard from './components/Dashbaord/Dashboard';
import Cars from './components/Dashbaord/Cars';
import Locations from './components/Dashbaord/Locations'
import LocationsHistory from './components/Dashbaord/LocationsHistory'
import Profile from './components/Dashbaord/Profile'
import Users from './components/Dashbaord/Users'

ReactDOM.render(
  <React.StrictMode>
    <Router>
      <Routes>
        <Route path="*" element={<Dashboard />} />
        <Route path="/CarFleet/Login" element={<Login />} />
        <Route path="/CarFleet/ResetPassword" element={<ForgotPassword />} />
        <Route path="/CarFleet/Dashboard" element={<Dashboard />} />
        <Route path="/CarFleet/Cars" element={<Cars />} />
        <Route path="/CarFleet/Locations" element={<Locations />} />
        <Route path="/CarFleet/LocationsHistory" element={<LocationsHistory />} />
        <Route path="/CarFleet/Account/Profile" element={<Profile />} />
        <Route path="/CarFleet/Users" element={<Users />} />
      </Routes>
    </Router>
  </React.StrictMode>,
  document.getElementById('root')
);

reportWebVitals();
