using System;
using System.Diagnostics;
using System.Windows.Forms;

class Program
{
    [STAThread]
    static void Main()
    {
        // Create a custom form for the GUI
        Form form = new Form
        {
            Text = "CutomInstaller",
            Width = 400,
            Height = 200,
            StartPosition = FormStartPosition.CenterScreen,
            FormBorderStyle = FormBorderStyle.FixedDialog,
            MaximizeBox = false,
            MinimizeBox = false
        };

        // Create a label for the message
        Label messageLabel = new Label
        {
            Text = "CustomInstaller has been successfully installed.\n This is just an example; you can remove this file using uninstall.bat.",
            AutoSize = false,
            TextAlign = System.Drawing.ContentAlignment.MiddleCenter,
            Dock = DockStyle.Top,
            Height = 80
        };
        form.Controls.Add(messageLabel);

        // Create the "OK" button
        Button okButton = new Button
        {
            Text = "OK",
            DialogResult = DialogResult.OK,
            Anchor = AnchorStyles.Bottom | AnchorStyles.Left,
            Width = 100,
            Height = 30,
            Left = 70,
            Top = 120
        };
        okButton.Click += (sender, e) => form.Close();
        form.Controls.Add(okButton);

        // Create the "Support" button
        Button supportButton = new Button
        {
            Text = "Learn More",
            Anchor = AnchorStyles.Bottom | AnchorStyles.Right,
            Width = 100,
            Height = 30,
            Left = 230,
            Top = 120
        };
        supportButton.Click += (sender, e) => Process.Start(new ProcessStartInfo
        {
            FileName = "https://github.com/danbenba/CustomInstaller",
            UseShellExecute = true
        });
        form.Controls.Add(supportButton);

        // Show the form
        Application.EnableVisualStyles();
        Application.Run(form);
    }
}