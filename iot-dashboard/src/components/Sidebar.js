import { useState } from "react";
import { Link } from "react-router-dom";
import DashboardIcon from "@mui/icons-material/Dashboard";
import DevicesIcon from "@mui/icons-material/Devices";
import AddBoxIcon from "@mui/icons-material/AddBox";
import ApiIcon from "@mui/icons-material/Api";
import FetchSensors from "./FetchSensors";

const Sidebar = () => {
  const [open, setOpen] = useState(true);
  const [sensors, setSensors] = useState([]);

  const toggleSidebar = () => {
    setOpen(!open);
  };

  return (
    <div className="flex">
      <div
        className={`${
          open ? "w-40" : "w-16" // Further adjusted width
        } bg-gray-800 h-screen p-4 pt-2 relative duration-300 shadow-md fixed top-0 left-0`}
      >
        <ul className="pt-2">
          <li className="flex items-center text-gray-300 cursor-pointer p-2 hover:bg-gray-700 text-lg">
            <DashboardIcon />
            <Link to="/" className={`${!open && "hidden"} ml-4`}>Dashboard</Link> {/* Increased left margin */}
          </li>
          <FetchSensors setSensors={setSensors} />
          {sensors.length > 0 ? (
            <li className="flex flex-col">
              <div className="flex items-center text-gray-300 cursor-pointer p-2 hover:bg-gray-700 text-lg">
                <DevicesIcon />
                <span className={`${!open && "hidden"} ml-4`}>Sensors</span> {/* Increased left margin */}
              </div>
              <ul className={`ml-4 ${!open ? "hidden" : ""}`}>
                {sensors.map((sensor, index) => (
                  <li key={index} className="text-gray-300 cursor-pointer p-2 hover:bg-gray-700 text-lg">
                    <Link to={`/devices/${sensor}`}>{sensor}</Link>
                  </li>
                ))}
              </ul>
            </li>
          ) : (
            <li className="text-gray-300 p-2 text-lg">No Sensors Available</li>
          )}
          <li className="flex items-center text-gray-300 cursor-pointer p-2 hover:bg-gray-700 text-lg">
            <AddBoxIcon />
            <Link to="/add-device" className={`${!open && "hidden"} ml-4`}>Add Device</Link> {/* Increased left margin */}
          </li>
          <li className="flex items-center text-gray-300 cursor-pointer p-2 hover:bg-gray-700 text-lg">
            <ApiIcon />
            <Link to="/api-usage" className={`${!open && "hidden"} ml-4`}>API Usage</Link> {/* Increased left margin */}
          </li>
        </ul>
      </div>
      <div className={`flex-1 ml-${open ? '40' : '16'} p-4`} style={{ marginTop: '60px' }}>
        {/* Main content goes here */}
      </div>
    </div>
  );
};

export default Sidebar;
